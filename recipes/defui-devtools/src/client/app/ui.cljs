(ns app.ui
  (:require
    [app.augments :include-macros true]
    [om.dom :as dom]
    [om.next :as om]
    [untangled.client.core :as uc]
    [untangled.client.ui :as ui :include-macros true]))

(ui/defui Child [:WithBooya :DerefFactory]
  static uc/InitialAppState
  (initial-state [cls params] {:id 0 :label (:label params)})
  static om/IQuery
  (query [this] [:id :label])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [id label]} (om/props this)]
      (dom/p nil label))))

(ui/defui Root [(:WithExclamation {:excl "SUCCESS"})]
  static uc/InitialAppState
  (initial-state [cls params]
    {:child (uc/initial-state Child {:label "Constructed Label"})})
  static om/IQuery
  (query [this] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key child]} (om/props this)]
      (js/console.log "react-key" react-key)
      (dom/div #js {:key react-key} (@Child child)))))
