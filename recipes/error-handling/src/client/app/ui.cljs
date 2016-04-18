(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.client.data-fetch :as df]))

(defui ^:once Child
  static om/IQuery
  (query [_] [[:untangled/server-error '_]])
  Object
  (render [this]
    (let [{:keys [untangled/server-error]} (om/props this)]
      (dom/div nil
        (dom/button #js {:onClick #(om/transact! this '[(my/mutation)])} "Click me for error!")
        (dom/div nil (str server-error))))))

(def ui-child (om/factory Child))

(defui ^:once Root
  static om/IQuery
  (query [_] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key child] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key} (ui-child child)))))

