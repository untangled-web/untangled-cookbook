(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once Datum
  static om/IQuery
  (query [_] [:db/id :datum/item])

  static om/Ident
  (ident [_ {:keys [db/id]}] [:datum/by-id id])

  Object
  (render [this]
    (let [{:keys [db/id datum/item]} (om/props this)]
      (dom/li #js {}
        (or item "There is no item.  WTF")))))

(def ui-datum (om/factory Datum {:keyfn :db/id}))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key
                 {:data (om/get-query Datum)}])

  Object
  (componentWillMount [this]
    (df/load-collection this [{:data (om/get-query Datum)}]))

  (render [this]
    (let [{:keys [ui/react-key data]
           :or   {ui/react-key "ROOT"}} (om/props this)]
      (dom/div #js {:key react-key}
        (dom/ul nil
          (map ui-datum data))))))
