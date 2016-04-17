(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key])
  Object
  (render [this]
    (let [{:keys [ui/react-key] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key}
          "TODO"))))
