(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            yahoo.intl-messageformat-with-locales))

(defui ^:once Person
  static om/IQuery
  (query [this] [:db/id :person/name :person/age :person/address])
  static om/Ident
  (ident [this props] [:people/by-id (:db/id props)])
  Object
  (render [this]
    (let [{:keys [db/id person/name person/address person/age]} (om/props this)]
      (dom/li nil (str name " (" age ") at " address)))))

(def ui-person (om/factory Person {:keyfn :db/id}))

(defui ^:once Root
  static InitialAppState
  (initial-state [cls params] {:people []})
  static om/IQuery
  (query [this] [:ui/react-key {:people (om/get-query Person)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key people]} (om/props this)]
      (dom/div #js {:key react-key}
        (dom/p nil "The people:")
        (dom/ul nil
          (map #(ui-person %) people))))))

