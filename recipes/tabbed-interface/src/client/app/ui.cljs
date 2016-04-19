(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

(defui ^:once SettingsTab
  static om/IQuery
  (query [this] [:which-tab :settings-content {[:tab-data-query '_] [:text]}])
  Object
  (render [this]
    (let [{:keys [settings-content tab-data-query]} (om/props this)]
      (dom/div nil
               settings-content
               (dom/p nil (:text tab-data-query))))))

(def ui-settings-tab (om/factory SettingsTab))

(defui ^:once MainTab
  static om/IQuery
  (query [this] [:which-tab :main-content])
  Object
  (render [this]
    (let [{:keys [main-content]} (om/props this)]
      (dom/div nil main-content))))

(def ui-main-tab (om/factory MainTab))

(defui ^:once TabUnion
  static om/IQuery
  (query [this] {:main (om/get-query MainTab) :settings (om/get-query SettingsTab)})
  static om/Ident
  (ident [this props] [(:which-tab props) :tab])
  Object
  (render [this]
    (let [{:keys [which-tab] :as props} (om/props this)]
      (js/console.log props)
      (dom/div nil

               (case which-tab
                 :main (ui-main-tab props)
                 :settings (ui-settings-tab props)
                 (dom/p nil "Missing tab!"))))))

(def ui-tabs (om/factory TabUnion))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:current-tab (om/get-query TabUnion)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key current-tab] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key}
               (dom/ul nil
                       (dom/li #js {:onClick #(om/transact! this '[(app/choose-tab {:tab :main})])} "Main")
                       (dom/li #js {:onClick #(om/transact! this '[(app/choose-tab {:tab :settings})
                                                                   ;; sample of what you would do to lazy load the tab content
                                                                   (app/lazy-load-tab {:tab :settings})])} "Settings"))
               (ui-tabs current-tab)))))
