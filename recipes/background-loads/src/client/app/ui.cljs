(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defn render-result [v] (dom/span nil v))

(defui ^:once Child
  static om/IQuery
  (query [this] [:id :name :long-query])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this] (let [{:keys [name name long-query]} (om/props this)]
                   (dom/div #js {:style #js {:display "inline" :float "left" :width "200px"}}
                     (dom/button #js {:onClick #(df/load-field this :long-query :background true)} "Load stuff")
                     (dom/div nil
                            name
                            (df/lazily-loaded render-result long-query))))))

(def ui-child (om/factory Child {:keyfn :id}))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:children (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key children] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key}
        (mapv ui-child children)))))
