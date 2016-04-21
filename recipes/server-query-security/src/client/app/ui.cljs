(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.client.data-fetch :as df]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once Person
  static om/IQuery
  (query [this] [:name :address :cc-number])
  Object
  (render [this]
    (let [{:keys [name address cc-number]} (om/props this)]
      (dom/div nil
        (dom/ul nil
          (dom/li nil (str "name: " name))
          (dom/li nil (str "address: " address))
          (dom/li nil (str "cc-number: " cc-number)))))))

(def ui-person (om/factory Person))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:person (om/get-query Person)} :untangled/server-error])
  Object
  (render [this]
    (let [{:keys [ui/react-key person server-error] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key}
        (when server-error
          (dom/p nil (pr-str "SERVER ERROR: " server-error)))
        (dom/button #js {:onClick #(df/load-data this [{:person (om/get-query Person)}])} "Query for person with credit card")
        (dom/button #js {:onClick #(df/load-data this [{:person (om/get-query Person)}] :without #{:cc-number})} "Query for person WITHOUT credit card")
        (ui-person person)))))
