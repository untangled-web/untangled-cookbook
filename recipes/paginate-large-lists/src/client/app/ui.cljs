(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once ListItem
  static om/IQuery
  (query [this] [:item/id])
  static om/Ident
  (ident [this props] [:items/by-id (:item/id props)])
  Object
  (render [this]
    (dom/li nil (str "Item " (-> this om/props :item/id)))))

(def ui-list-item (om/factory ListItem {:keyfn :item/id}))

(defui ^:once LargeList
  static om/IQuery
  (query [this] [:start :total-results {:items (om/get-query ListItem)}])
  Object
  (render [this]
    (let [{:keys [start total-results items]} (om/props this)]
      (dom/div nil
        (dom/h2 nil (str "Items " start "+ (of " total-results ")"))
        (dom/button #js {:onClick #(om/transact! this '[(prior-page) (fill-cache)])} "Prior Page")
        (dom/button #js {:onClick #(om/transact! this '[(next-page) (fill-cache)])} "Next Page")
        (mapv ui-list-item items)))))


(def ui-list (om/factory LargeList))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:current-page (om/get-query LargeList)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key current-page] :or {ui/react-key "ROOT"}} (om/props this)]
      (dom/div #js {:key react-key} (ui-list current-page)))))
