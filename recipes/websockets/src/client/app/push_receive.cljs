(ns app.push-receive
  (:require [app.ui :as ui]
            [om.next :as om]
            [untangled.client.core :refer [refresh]]
            [untangled.client.data-fetch :as df]
            [untangled.websockets.networking :as n]))

(defmethod n/push-received :user/left [{:keys [reconciler] :as app} {:keys [msg]}]
  (let [state (om/app-state reconciler)
        channel-ident (get @state :current-channel)
        user-ident [:user/by-id (:db/id msg)]]
    (swap! state update :user/by-id dissoc (:db/id msg))
    (swap! state update :app/users
      (fn [users] (into [] (remove #(= user-ident %)) users)))
    (swap! state update-in (conj channel-ident :channel/users)
      (fn [users] (into [] (remove #(= user-ident %)) users)))
    (refresh app)))

(defmethod n/push-received :user/new [{:keys [reconciler] :as app} {:keys [msg]}]
  (let [state (om/app-state reconciler)
        channel-ident (get @state :current-channel)
        user-ident [:user/by-id (:db/id msg)]]
    (swap! state assoc-in user-ident msg)
    (swap! state update :app/users (fnil conj []) user-ident)
    (swap! state update-in (conj channel-ident :channel/users) (fnil conj []) user-ident)
    (refresh app)))

(defmethod n/push-received :message/new [{:keys [reconciler] :as app} {:keys [msg]}]
  (let [state (om/app-state reconciler)
        channel-ident (get @state :current-channel)
        message-ident [:message/by-id (:db/id msg)]]
    (swap! state assoc-in message-ident msg)
    (swap! state update-in (conj channel-ident :channel/messages) (fnil conj []) message-ident)
    (refresh app)))
