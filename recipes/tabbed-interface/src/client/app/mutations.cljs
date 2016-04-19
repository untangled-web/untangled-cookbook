(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.client.data-fetch :as df]
            [om.next :as om]))

;; When to consider the data missing? Check the state and find out.
(defn missing-tab? [state tab] (not (:tab-data-query @state)))

(defmethod m/mutate 'app/lazy-load-tab [{:keys [state]} k {:keys [tab]}]
  (when (missing-tab? state tab)
    ; remote must be an AST for (app/load), which tells Untangled to check the load queue.
    {:remote (om/query->ast '[(app/load)])
     :action (fn []
               ; This puts the real query into the load queue
               (df/load-data-action state [:tab-data-query])
               ; anything else you need to do for this transaction
               )}))

(defmethod m/mutate 'app/choose-tab [{:keys [state]} k {:keys [tab]}]
  {:action (fn [] (swap! state assoc-in [:current-tab 0] tab))})

