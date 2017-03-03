(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            yahoo.intl-messageformat-with-locales
            [untangled.client.data-fetch :as df]
            [untangled.client.mutations :as m :refer [defmutation]]))

(declare Item)

(defn set-item-loading [item loading?]
  (assoc item :ui/refreshing? loading?))

(defn update-item [state-map item-id f & args]
  (apply update-in state-map [:items/by-id item-id] f args))

(defmutation mark-item-loaded
  [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-item id set-item-loading false)))

(defmutation refresh-item
  [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state (fn [state-map] (-> state-map
                                   (update-item id set-item-loading true)))
      (df/load-action state [:items/by-id id] Item {:post-mutation        `mark-item-loaded
                                                    :post-mutation-params {:id id}})))
  (remote [env] (df/remote-load env)))

(defui ^:once Item
  static om/IQuery
  ;; The :ui/fetch-state is queried so the parent (Child in this case) lazy load renderer knows what state the load is in
  (query [this] [:db/id :item/label :ui/refreshing? :ui/fetch-state])
  static om/Ident
  (ident [this props] [:items/by-id (:db/id props)])
  Object
  (render [this]
    (let [{:keys [db/id item/label ui/refreshing?]} (om/props this)]
      (dom/div nil label
        (if refreshing?
          "(reloading...)"
          (dom/button #js {:onClick #(om/transact! this `[(refresh-item {:id ~id})])} "Refresh"))))))

(def ui-item (om/factory Item {:keyfn :db/id}))

(defui ^:once Child
  static om/IQuery
  ;; The :ui/fetch-state is queried so the parent (Panel) lazy load renderer knows what state the load is in
  (query [this] [:ui/fetch-state :label {:items (om/get-query Item)}])
  static om/Ident
  (ident [this props] [:ui :child])
  Object
  (render [this]
    (let [{:keys [label items]} (om/props this)
          render-item (fn [idx i] (df/lazily-loaded ui-item i))
          render-list (fn [items] (map-indexed render-item items))]
      (dom/div nil
        (dom/p nil label)
        (df/lazily-loaded render-list items
          :not-present-render (fn [items] (dom/button #js {:onClick #(df/load-field this :items)} "Load Items")))))))

(def ui-child (om/factory Child))

(defui ^:once Panel
  static om/IQuery
  (query [this] [[:ui/loading-data '_] {:child (om/get-query Child)}])
  static om/Ident
  (ident [this props] [:ui :panel])
  Object
  (render [this]
    (let [{:keys [ui/loading-data child] :as props} (om/props this)]
      (dom/div nil
        (dom/div #js {:style #js {:float "right" :display (if loading-data "block" "none")}} "GLOBAL LOADING")
        (df/lazily-loaded ui-child child
          :not-present-render (fn [_] (dom/button #js {:onClick #(df/load-field this :child)} "Load Child")))))))

(def ui-panel (om/factory Panel))

; Note: Kinda hard to do idents/lazy loading right on root...so generally just have root render a div for forced react
; key refresh and then render a child that has the rest.
(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/loading-data :ui/react-key {:panel (om/get-query Panel)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key panel] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key} (ui-panel panel)))))
