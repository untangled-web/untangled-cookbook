(ns app.components.web-sockets
  (:require
   [bidi.bidi :as bidi]
   [clojure.core.async :as async :refer [<! <!! chan go thread]]
   [com.stuartsierra.component :as component]
   [taoensso.timbre :as timbre]
   [taoensso.sente :as sente]
   [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
   [ring.middleware.params :as params]
   [ring.middleware.keyword-params :as keyword-params]
   [untangled.server.impl.components.handler :refer [index api generate-response]])
  (:gen-class)
  (:import (clojure.lang ExceptionInfo)))

(defn wrap-dependencies
  "Wrap the handler such that the given dependency value can be obtained from the request
   using get-dependencies and the specified key-path. Multiple wrap-dependencies can be chained in your middleware.

   Parameters:
   * `handler` - The handler to wrap
   * `pairs` - Pairs of key paths and values to add to the dependencies.

   Example:

         (wrap-dependencies handler
              [:top :k2] value
              [:top :k3] value2
              )
   "
  [handler & pairs]
  (assert (sequential? (first pairs)) "first item in pairs must be a list of vector")
  (fn [req]
    (let [new-req (reduce (fn [r [key-path value]]
                            (let [path (conj (apply vector :dashboard/dependencies key-path) :dependency/value)
                                  new-req (update-in r path (fn [n] value))]
                              new-req)) req (partition 2 pairs))]
      (handler new-req))))

(defn get-dependency
  "
  Get a dependency previously injected by wrap-dependencies.
   Parameters
   * `req` the incoming request
   * `key-path` a vector or list that represents a location that was used in the injection
  "
  [req key-path]
  (assert (sequential? key-path) "Key-path must be vector or list")
  (let [path (conj (apply vector :dashboard/dependencies key-path) :dependency/value)]
    (get-in req path)))

;; SAMPLE MESSAGE FROM CLIENT self-identified as "930" that we have assigned user-id 1 to (via (<! recv-channel))
;{:?reply-fn      (fn [edn] ...plumbing to client ...),
; :ch-recv        #<ManyToManyChannel clojure.core.async.impl.channels.ManyToManyChannel@3926ef92>,
; :client-id      "930",
; :connected-uids #<Atom@1bb933d: {:ws #{1}, :ajax #{}, :any #{1}}>,
; :uid            1,
; :event          [:a/b {:a 22}],
; :id             :a/b,
; :ring-req
;                 {:remote-addr          "0:0:0:0:0:0:0:1",
;                  :params               {:client-id "930"},
;                  :datahub/credentials  {:real-user nil, :effective-user nil, :realm nil},
;                  :route-params         {},
;                  :headers              {"origin" "http://localhost:4001", "host" "localhost:3000", ...}
;                  :websocket?           true,
;                  :query-params         {"client-id" "930"},
;                  :datahub/dependencies {:databases ...}
;                  :server-name          "localhost",
;                  :query-string         "client-id=930",
;                  :scheme               :http,
;                  :request-method       :get},
; :?data          {:a 22},
; :send-fn #<sente$make_channel_socket_BANG_$send_fn__29153 taoensso.sente$make_channel_socket_BANG_$send_fn__29153@1d0c0cb4>}

; Message from client: [ target-keyword { :sub-target kw :content edn-msg } ]
; message handed to message-received: { :reply-fn (fn [edn] ...)   ; optional...if it is there, should be called with response
;                                       :content edn-value
;                                     }

;; NOTE: response-channel is (partial send! user-id)
(defmulti message-received
          "The primary multi-method to define methods for in order to receive client messages.

          The client will send a message of the form (via Sente):

               [:target/kw { :command :kw ...any other data... }]

          The client can include a timeout and reply callback:

               (sente/send! [:dataservice/todomvc { :command :subscribe :entity 23 }]  ; message
                            8000  ; timeout
                            (fn [entity-data] ...)) ; callback for reply

          It is recommended that your client components wrap the details of send/reply is a more friendly API for your abstractions.

          On the server, such a client message would result in a call to this multimethod as follows:

               (message-received [:dataservice/todomvc :subscribe] [message] ...)


          where the message parameter will contain the following key/value pairs:

                  :target    target
                  :command   command
                  :client-id ID of the client
                  :content   actual_message_from_client ; including :command
                  :push-fn   (fn [edn] ...) ; can be used at future time to send server-push message to client
                  :reply-fn  (fn [edn] ...) ; may be missing if client did not want a response.

          The push-fn will be usable UNLESS you later receive a :client-disconnect lifecycle command, indicating
          the client ID has disappeared, and can no longer be reached. If you save/close over this value, you
          MUST watch for disconnects and throw away all state related to that client including the push function.

          By defining dispatch for your own target keyword (and command subset), you may use this multimethod
          to implement whatever protocol you desire over the single shared websocket.
          "
          (juxt :target #(get % :action :default)))

(defprotocol IWebSocketRegistry
  (register-connection [this client-id]
    "Register a new client when a web-socket connection is made")
  (drop-connection [this client-id]
    "Drop a client connection when a web-socket connection is disconnected")
  (send-to-client [this client-id message]
    "Send a message to a particular client")
  (send-to-all [this message]
    "Send a message to a all clients"))

(defn event-loop [websocket-registry]
  (timbre/info "Websocket event thread started.")
  (let [ch-recv (:ch-recv websocket-registry)]
    (go (loop [{:keys [?reply-fn client-id uid id ?data] :as data} (<! ch-recv)]
          (if data
            (let [target  id ; Sente uses the key "id" for what we want to call "target"
                  action  (or (:action ?data) :default)
                  command (or (:command ?data) :default)
                  message (cond-> {:target      target
                                   :action      action
                                   :command     command
                                   :credentials {}
                                   :content     (:content ?data)
                                   :client-id   uid}
                            ?reply-fn (assoc :reply-fn ?reply-fn))] ;(message-received event client-id ring-req websocket-registry)
              (try
                (message-received message)
                (catch Exception e
                  (timbre/error "Caught exception in message-received: " (.getMessage e))))
              (recur (<! ch-recv)))
            (timbre/info "Websocket event thread stopped."))))))

(defn wrap-web-socket [component handler]
  (-> handler
      (wrap-dependencies [:web-socket :only] component)
      (keyword-params/wrap-keyword-params)
      (params/wrap-params)))

(defrecord WebSocketRegistry [handler
                              config
                              connected-services            ; atom containing a set of target keywords that have registered multimethods
                              clients                       ; atom containing all connected clients
                              groups                        ; atom containing all groups
                              next-id                       ;next uid for client tab
                              ring-ajax-post                ; ring hook-ups
                              ring-ajax-get-or-ws-handshake
                              ch-recv                       ; incoming messages
                              chsk-send!                    ; server push by uid
                              connected-uids]
  IWebSocketRegistry
  (register-connection [this client-id]
    (swap! clients conj client-id))

  (drop-connection [this client-id]
    (swap! clients disj client-id)
    (let [groups             @(:groups this)
          groups-with-client (keys (filter #(contains? % client-id) groups))]
      (map (fn [group-id]
             (swap! (:groups this) update-in [group-id] (disj client-id)))
           groups-with-client)))

  (send-to-client [this client-id message]
    (when (not (nil? client-id))
      (let [send-fn (:chsk-send! this)]
        (send-fn client-id [:api/message message]))))

  (send-to-all [this message]
    (let [send-fn     (:chsk-send! this)
          all-clients @(:clients this)]
      (doall (map (fn [client-id] (send-to-client this client-id message)) all-clients))))

  component/Lifecycle
  (start [component] []
    (timbre/info "Websocket system starting up.")
    (let [pre-hook                 (.get-pre-hook handler)
          id-atom                  (atom 0)
          {:keys [ch-recv
                  send-fn
                  ajax-post-fn
                  ajax-get-or-ws-handshake-fn
                  connected-uids]} (sente/make-channel-socket!
                                     sente-web-server-adapter
                                     {:user-id-fn (fn [_request] (swap! id-atom inc))})]
      (let [{:keys [api-parser
                    env]} (:handler component)
            rv            (assoc component
                            :connected-services (atom #{})
                            :clients (atom #{})
                            :groups (atom {})
                            :next-id id-atom
                            :ring-ajax-post ajax-post-fn
                            :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
                            :ch-recv ch-recv                 ; ChannelSocket's receive channel
                            :chsk-send! send-fn              ; ChannelSocket's send API fn
                            :connected-uids connected-uids)]; Watchable, read-only atom
        (.set-pre-hook! handler
          (comp pre-hook
            (partial wrap-web-socket rv)))

        (defmethod message-received :default [message]
          (timbre/error (str "Received message " message ", but no receiver wanted it!")))

        (defmethod message-received [:api/message :send-message] [message]
          (timbre/debug "Received and api call: " message)
          (let [result (api {:transit-params (:content message)
                             :parser         api-parser
                             :env            env})]
            (timbre/info "Api result: " result)
            (send-to-all rv result)))

        (defmethod message-received [:chsk/uidport-open :default] [message]
          (timbre/debug "Port opened by client" (:client-id message))
          (register-connection rv (:client-id message)))

        (defmethod message-received [:chsk/uidport-close :default] [message]
          (timbre/debug "Connection closed" (:client-id message))
          (drop-connection rv (:client-id message)))

        (defmethod message-received [:chsk/ws-ping :default] [message]
          (timbre/info "Ping from client" (:client-id message)))
        (event-loop rv)
        rv)))

  (stop [component] []
    (timbre/info "Websocket system shutting down.")
    (remove-all-methods message-received)
    (async/close! ch-recv)
    (map->WebSocketRegistry {})))

(defn make-web-socket-registry []
  (component/using
    (map->WebSocketRegistry {})
    [:handler :config]))

(defn route-handlers [req env match]
  (let [websocket                     (get-dependency req [:web-socket :only])
        ring-ajax-get-or-ws-handshake (get websocket :ring-ajax-get-or-ws-handshake)
        ring-ajax-post                (get websocket :ring-ajax-post)]
    (case (:request-method req)
      :get  (try (ring-ajax-get-or-ws-handshake req)
                 (catch Exception e (.printStackTrace e System/out)))
      :post (ring-ajax-post req))))
