(ns app.mutations
  (:require [untangled.client.mutations :as m]))

(defmethod m/mutate 'app/delete-item [{:keys [state]} k {:keys [id]}]
  {:action (fn []
             (letfn [(filter-item [list id] (filterv #(not= (second %) id) list))]
               (swap! state update-in [:lists 1 :list/items] filter-item id)))})

