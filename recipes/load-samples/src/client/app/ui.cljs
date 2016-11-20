(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once Person
  static om/IQuery
  (query [this] [:db/id :person/name :person/age-ms])
  static om/Ident
  (ident [this props] [:people/by-id (:db/id props)])
  Object
  (render [this]
    (let [{:keys [db/id person/name person/age-ms] :as props} (om/props this)]
      (dom/li nil
        (str name " (last queried at " age-ms ")")
        (dom/button #js {:onClick (fn []
                                    ; Load relative to an ident (of this component). This will refresh the entity in the db.
                                    (df/load this (om/ident this props) Person))} "Update")))))

(def ui-person (om/factory Person {:keyfn :db/id}))

(defui ^:once People
  static om/IQuery
  (query [this] [:people/kind {:people (om/get-query Person)}])
  static om/Ident
  (ident [this props] [:lists/by-type (:people/kind props)])
  Object
  (render [this]
    (let [{:keys [people]} (om/props this)]
      (dom/ul nil (map ui-person people)))))

(def ui-people (om/factory People {:keyfn :people/kind}))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key
                 {:enemies (om/get-query People)}
                 {:friends (om/get-query People)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key friends enemies]} (om/props this)]
      (dom/div #js {:key react-key}
        (dom/h4 nil "Friends")
        (ui-people friends)
        (dom/h4 nil "Enemies")
        (ui-people enemies)))))

