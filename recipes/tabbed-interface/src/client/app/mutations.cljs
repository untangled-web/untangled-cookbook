(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.client.data-fetch :as df]
            [om.next :as om]))

;; This is all you need to "change tabs"
(defmethod m/mutate 'app/choose-tab [{:keys [state]} k {:keys [tab]}]
  {:action (fn [] (swap! state assoc-in [:current-tab 0] tab))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LAZY LOADING TAB CONTENT
;; This is the shape of what to do. We define a method that can examine the
;; state to decide if we want to trigger a load. Then we define a mutation
;; that the UI can call during transact (see the transact! call for Settings on Root in ui.cljs).
;; The mutation itself (app/lazy-load-tab) below uses a data-fetch helper function to
;; set :remote to the right thing, and can then give one or more load-data-action's to
;; indicate what should actually be retrieved. The server implementation is trivial in
;; this case. See api.clj.

;; When to consider the data missing? Check the state and find out.
(defn missing-tab? [state tab] (not (:tab-data-query @state)))

(defmethod m/mutate 'app/lazy-load-tab [{:keys [state] :as env} k {:keys [tab]}]
  (when (missing-tab? state tab)
    ; remote must be the value returned by data-fetch remote-load on your parsing environment.
    {:remote (df/remote-load env)
     :action (fn []
               ; Specify what you want to load as one or more calls to load-data-action:
               (df/load-data-action state [:tab-data-query])
               ; anything else you need to do for this transaction
               )}))


