(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once ListItem
  static om/IQuery
  (query [this] [:item/id :item/label])
  static om/Ident
  (ident [this props] [:item/by-id (:item/id props)])
  Object
  (render [this]
    (let [{:keys [on-delete]} (om/get-computed this)
          {:keys [item/id item/label]} (om/props this)]
      (dom/li nil
              label
              (dom/button #js {:onClick #(on-delete id)} "X")))))

(def ui-list-item (om/factory ListItem :item/id))

(defui ^:once List
  static om/IQuery
  (query [this] [:list/name {:list/items (om/get-query ListItem)}])
  Object
  (render [this]
    (let [{:keys [list/name list/items]} (om/props this)]
      (dom/div nil
               (dom/h4 nil name)
               (dom/ul nil
                       (map ui-list-item items))))))

(def ui-list (om/factory List))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:main-list (om/get-query List)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key main-list] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key} (ui-list main-list)))))
