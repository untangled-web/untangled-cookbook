(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.client.data-fetch :as df]))

(defui ^:once Child
  static om/IQuery
  ;; you can query for the server-error using a link from any component that composes to root
  (query [_] [[:untangled/server-error '_] :ui/button-disabled])
  Object
  (render [this]
    (let [{:keys [untangled/server-error ui/button-disabled]} (om/props this)]
      (dom/div nil
        ;; declare a tx/fallback in the same transact call as the mutation
        ;; if the mutation fails, the fallback will be called
        (dom/button #js {:onClick  #(om/transact! this '[(my/mutation) (tx/fallback {:action button/disable})])
                         :disabled button-disabled}
          "Click me for error!")
        (dom/div nil (str server-error))))))

(def ui-child (om/factory Child))

(defui ^:once Root
  static om/IQuery
  (query [_] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key child] :or {ui/react-key "ROOT"}} (om/props this)]
      (dom/div #js {:key react-key} (ui-child child)))))

