(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.client.core :as uc]))

(defmethod m/mutate 'app/add-person [{:keys [state]} k {:keys [id name age]}]
  {:remote true
   :action (fn []
             (let [ident [:person/by-id id]]
               (swap! state assoc-in ident {:db/id id :person/name name :person/age age})
               (uc/integrate-ident! state ident :append [:people])))})
