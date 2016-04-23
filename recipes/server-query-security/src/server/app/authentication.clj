(ns app.authentication
  (:require [com.stuartsierra.component :as c]
            [app.security :as security]
            [untangled.server.impl.components.handler :as h]
            [taoensso.timbre :as timbre]))

(defrecord Authentication [handler]
  c/Lifecycle
  (start [this]
    (timbre/info "Hooking into pre-processing to add user info")
    (let [old-pre-hook (h/get-pre-hook handler)
          new-hook (fn [ring-handler] (fn [req] ((old-pre-hook ring-handler) (assoc req :user {:username "Tony"}))))]
      (h/set-pre-hook! handler new-hook))
    this)
  (stop [this] this))

(defn make-authentication []
  (c/using (map->Authentication {}) [:handler]))

(defprotocol Auth
  (can-access-entity? [this user key entityid] "Check if the given user is allowed to access the entity designated by the given key and entity id")
  (authorized-query? [this user top-key query] "Check if the given user is allowed to access all of the data in the query that starts at the given join key"))

(defrecord Authorizer []
  c/Lifecycle
  (start [this] this)
  (stop [this] this)
  Auth
  (can-access-entity? [this user key entityid] (security/authorized-root-entity? user key entityid))
  (authorized-query? [this user top-key query] (security/authorized-query? query top-key)))

(defn make-authorizer [] (map->Authorizer {})) 

