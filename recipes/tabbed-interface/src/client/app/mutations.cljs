(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.client.data-fetch :as df]
            [om.next :as om]))

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

(defmethod m/mutate 'app/choose-tab [{:keys [state]} k {:keys [tab]}]
  {:action (fn [] (swap! state assoc-in [:current-tab 0] tab))})

