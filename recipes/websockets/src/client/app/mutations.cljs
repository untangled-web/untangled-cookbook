(ns app.mutations
  (:require [untangled.client.mutations :as m]))

(defmethod m/mutate 'datum/add [{:keys [state ast] :as env} _ params]
  {:remote ast
   :action (fn []
             (let [{:keys [db/id]} params
                   ident           [:datum/by-id id]]
               (swap! state assoc-in ident params)
               (swap! state update-in [:data] (fnil conj []) ident)))})

(defmethod m/mutate 'app/subscribe [{:keys [ast]} _ _]
  {:remote ast})
