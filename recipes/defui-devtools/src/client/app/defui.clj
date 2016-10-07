(ns app.defui
  (:require
    [untangled.client.ui :as ui]))

(defmethod ui/defui-ast-xform :WithBooya [ctx body params]
  (-> body
    (update-in ["Object" :methods "render" :body]
      (fn [body]
        (conj (vec (butlast body))
              `(om.dom/div nil
                 (om.dom/h1 nil "booya")
                 ~(last body)))))))

(defmacro defui [ui-name mixins & body]
  (ui/defui* ui-name mixins body &form &env))
