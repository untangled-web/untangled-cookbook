(ns app.ui
  (:require
    [om.dom :as dom]
    [om.next :as om :refer-macros [defui]]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.core :refer [InitialAppState initial-state]]
    [untangled.client.ui :include-macros true :as ui]
    yahoo.intl-messageformat-with-locales))

(ui/defui Fancy {}
  Object
  (render [this]
    (dom/p nil "FANCY DEFUI - INCREMENT ME TO SEE CHANGES IN YOUR DEFUI-XFORM - 21")))

(defui ^:once Child
  static InitialAppState
  (initial-state [cls params] {:id 0 :label (:label params)})
  static om/IQuery
  (query [this] [:id :label])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [id label]} (om/props this)]
      (dom/div nil
        (@Fancy)
        (dom/p nil label)))))

(def ui-child (om/factory Child))

(defui ^:once Root
  static InitialAppState
  (initial-state [cls params]
    {:child (initial-state Child {:label "Constructed Label"})})
  static om/IQuery
  (query [this] [{:child (om/get-query Child)} :ui/react-key])
  Object
  (render [this]
    (let [{:keys [child ui/react-key]} (om/props this)]
      (dom/div #js {:key react-key}
        (ui-child child)))))
