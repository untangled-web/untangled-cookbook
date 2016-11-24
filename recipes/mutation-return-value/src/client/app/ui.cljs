(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            yahoo.intl-messageformat-with-locales))

(defui ^:once Child
  static InitialAppState
  (initial-state [cls params] {:id 0 :volume 5})
  static om/IQuery
  (query [this] [:id :volume])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [id volume]} (om/props this)]
      (dom/div nil
        (dom/p nil "Current volume: " volume)
        (dom/button #js {:onClick #(om/transact! this [(list 'crank-it-up {:value volume})])} "+")))))

(def ui-child (om/factory Child))

(defui ^:once Root
  static InitialAppState
  (initial-state [cls params]
    {:child (initial-state Child {})})
  static om/IQuery
  (query [this] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key child]} (om/props this)]
      (dom/div #js {:key react-key} (ui-child child)))))

