(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            yahoo.intl-messageformat-with-locales))

(defui ^:once Child
  static InitialAppState
  (initial-state [cls _] {:id 0})
  static om/IQuery
  (query [this] [:id])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (initLocalState [this] {})
  (componentDidMount [this] (js/console.log :mount (om/props this)))
  (componentWillReceiveProps [this props] (js/console.log :will props))
  (componentWillUpdate [this next-props next-state] (js/console.log :will next-props :st next-state :oldprops (om/props this)))
  (render [this]
    (dom/canvas #js {:width "100px" :height "100px" :style #js {:border "1px solid black"}})))

(def ui-child (om/factory Child))

(defui ^:once Root
  static InitialAppState
  (initial-state [cls params]
    {:ui/react-key "K" :child (initial-state Child nil)})
  static om/IQuery
  (query [this] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key child]} (om/props this)]
      (js/console.log react-key)
      (dom/div #js {:key react-key}
        (dom/p nil "Ho")
        (ui-child child)))))

