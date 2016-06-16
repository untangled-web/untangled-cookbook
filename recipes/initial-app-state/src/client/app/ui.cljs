(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            yahoo.intl-messageformat-with-locales))

(defui ^:once Label
  static om/IQuery
  (query [this] [:id :value])
  static om/Ident
  (ident [this props] [:labels/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [value]} (om/props this)]
      (dom/span nil value))))

(def ui-label (om/factory Label {:keyfn :id}))

(defui ^:once Child
  static InitialAppState
  (initial-state [clz params] {:ui/checked true})
  static om/IQuery
  (query [this] [:id {:label (om/get-query Label)}])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [label]} (om/props this)]
      (dom/div #js {:className "xyz"}
        (dom/span #js {:className "a"} (ui-label label))))))

(def ui-child (om/factory Child {:keyfn :id}))

(defui ^:once Settings
  static InitialAppState
  (initial-state [clz params] {:type :settings :id :singleton})
  static om/IQuery
  (query [this] [:type :id])
  Object
  (render [this]
    (dom/p nil "SETTINGS")))

(def ui-settings (om/factory Settings {:keyfn :type}))

(defui ^:once Main
  static InitialAppState
  (initial-state [clz params] {:type :main :id :singleton})
  static om/IQuery
  (query [this] [:type :id])
  Object
  (render [this]
    (dom/p nil "Main")))

(def ui-main (om/factory Main {:keyfn :type}))

(defui ^:once PaneSwitcher
  static InitialAppState
  (initial-state [clz params] (initial-state Main nil))
  static om/IQuery
  (query [this] {:settings (om/get-query Settings) :main (om/get-query Main)})
  static om/Ident
  (ident [this props] [(:type props) (:id props)])
  Object
  (render [this]
    (let [{:keys [type] :as props} (om/props this)]
      (case type
        :settings (ui-settings props)
        :main (ui-main props)
        (dom/p nil "NO PANE!")))))

(def ui-panes (om/factory PaneSwitcher {:keyfn :type}))

(defui ^:once Root
  static InitialAppState
  (initial-state [clz params] {:ui/react-key "abc"
                                  :children     []
                                  :panes        (initial-state PaneSwitcher nil)})
  static om/IQuery
  (query [this] [:ui/react-key :value {:children (om/get-query Child)}
                 {:panes (om/get-query PaneSwitcher)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key value children panes] :or {value ""}} (om/props this)]
      (dom/div #js {:key react-key}
        (dom/input #js {:type "text" :value value})
        (dom/button #js {:onClick (fn [evt] (om/transact! this '[(nav/settings)]))} "Go to settings")
        (dom/button #js {:onClick (fn [evt] (om/transact! this '[(nav/main)]))} "Go to main")
        (dom/button #js {:onClick (fn [evt] (om/transact! this '[(set-to-tony)]))} "Set to Tony")
        (ui-panes panes)
        (mapv ui-child children)))))


(comment
  (uc/merge-state! @app.core/app Child {:id 1 :label {:id 41 :value "Blammo!"}} :append [:children])
  (uc/merge-state! @app.core/app Child {:id 2 :label {:id 42 :value "Boo!"}} :replace [:children 0])
  (uc/merge-state! @app.core/app Child {:id 3 :label {:id 43 :value "oogle!"}} :prepend [:children]))
