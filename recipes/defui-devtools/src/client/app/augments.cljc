(ns app.augments
  (:require
    [untangled.client.defui-augment :refer [defui-augment]]))

(defmethod defui-augment :WithBooya [ctx body params]
  (-> body
    (update-in [:impls "Object" :methods "render" :body]
      (fn [body]
        (conj (vec (butlast body))
              `(om.dom/div nil
                 (om.dom/h1 nil "BOOYA")
                 ~(last body)))))))
