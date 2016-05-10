(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            app.mutations))

(defui ^:once Item
  static om/IQuery
  (query [this] [:item/id :item/label])
  static om/Ident
  (ident [this props] [:items (:item/id props)])
  Object
  (render [this]
    (let [{:keys [on-delete]} (om/get-computed this)
          {:keys [item/id item/label]} (om/props this)]
      (dom/li nil
              label
              (dom/button #js {:onClick #(on-delete id)} "X")))))

(def ui-list-item (om/factory Item :item/id))

(defui ^:once ItemList
  static om/IQuery
  (query [this] [:list/id :list/name {:list/items (om/get-query Item)}])
  static om/Ident
  (ident [this props] [:lists (:list/id props)])
  Object
  (render [this]
    (let [{:keys [list/name list/items]} (om/props this)
          item-props (fn [i] (om/computed i {:on-delete #(om/transact! this `[(app/delete-item {:id ~(:item/id i)})])}))]
      (dom/div nil
               (dom/h4 nil name)
               (dom/ul nil
                       (map #(ui-list-item (item-props %)) items))))))

(def ui-list (om/factory ItemList))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:main-list (om/get-query ItemList)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key main-list] :or {ui/react-key "ROOT"}} (om/props this)]
      (dom/div #js {:key react-key} (ui-list main-list)))))
