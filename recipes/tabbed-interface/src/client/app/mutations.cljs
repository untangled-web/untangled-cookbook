(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.client.data-fetch :as df]
            [om.next :as om]
            ))

(defn missing-tab? [s t] false)                             ;; TODO: When to consider the data missing?

(defmethod m/mutate 'app/lazy-load-tab [{:keys [state]} k {:keys [tab]}]
  (when (missing-tab? state tab)
    ; just include (app/load). OK to combined with other remoting
    {:remote (om/query->ast '[(app/load)])
     :action (fn []
               ; This puts the real query into the load queue
               (df/load-data-action state '[:tab-data-query])
               ; anything else you need to do for this transaction
               )}))

(defmethod m/mutate 'app/choose-tab [{:keys [state]} k {:keys [tab]}]
  {:action (fn [] (swap! state assoc-in [:current-tab 0] tab))})

