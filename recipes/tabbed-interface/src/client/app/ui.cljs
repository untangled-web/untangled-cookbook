(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]))

; This UI component uses a "link"...a special ident with '_ as the ID. This indicates the item is at the database
; root, not inside of the "settings" database object. This is not needed as a matter of course...it is only used
; for convenience (since it is trivial to load something into the root of the database)
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

; This is the main trick: a component that serves to pick which UI/query to render based on which thing
; the ident of :current-tab points to in the database. The :current-tab name means nothing, of course, it
; was just a convenient name (see Root, which queried for it and passed the result here).
; IMPORTANT:
; 1. query must be a union, which is a map keyed by "object type" with the query to use for that object
; 2. The ident MUST derive the correct db ident for whatever you got in props
; 3. You are responsible for calling the correct UI from render to properly render the thing you got
(defui ^:once TabUnion
  static om/IQuery
  (query [this] {:main (om/get-query MainTab) :settings (om/get-query SettingsTab)})
  static om/Ident
  (ident [this props] [(:which-tab props) :tab])
  Object
  (render [this]
    (let [{:keys [which-tab] :as props} (om/props this)]
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
               ; The selection of tabs can be rendered in a child, but the transact! must be done from the parent (to
               ; ensure proper re-render of the tab body). See om/computed for passing callbacks.
               (dom/ul nil
                       (dom/li #js {:onClick #(om/transact! this '[(app/choose-tab {:tab :main})])} "Main")
                       (dom/li #js {:onClick #(om/transact! this '[(app/choose-tab {:tab :settings})
                                                                   ; extra mutation: sample of what you would do to lazy load the tab content
                                                                   (app/lazy-load-tab {:tab :settings})])} "Settings"))
               (ui-tabs current-tab)))))
