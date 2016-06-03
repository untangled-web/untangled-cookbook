(ns app.mutations
  (:require [untangled.client.mutations :as m]))

;;; PUSH MUTATIONS

(defmethod m/mutate 'push/user-left [{:keys [state ast] :as env} _ {:keys [msg]}]
  {:action (fn []
             (let [channel-ident (get @state :current-channel)
                   user-ident [:user/by-id (:db/id msg)]]
               (swap! state update :user/by-id dissoc (:db/id msg))
               (swap! state update :app/users
                      (fn [users] (into [] (remove #(= user-ident %)) users)))
               (swap! state update-in (conj channel-ident :channel/users)
                      (fn [users] (into [] (remove #(= user-ident %)) users)))))})

(defmethod m/mutate 'push/user-new [{:keys [state ast] :as env} _ {:keys [msg]}]
  {:action (fn []
             (let [channel-ident (get @state :current-channel)
                   user-ident [:user/by-id (:db/id msg)]]
               (swap! state assoc-in user-ident msg)
               (swap! state update :app/users (fnil conj []) user-ident)
               (swap! state update-in (conj channel-ident :channel/users) (fnil conj []) user-ident)))})

(defmethod m/mutate 'push/message-new [{:keys [state ast] :as env} _ {:keys [msg]}]
  {:action (fn []
             (let [channel-ident (get @state :current-channel)
                   message-ident [:message/by-id (:db/id msg)]]
               (swap! state assoc-in message-ident msg)
               (swap! state update-in (conj channel-ident :channel/messages) (fnil conj []) message-ident)))})

;;; CLIENT MUTATIONS

(defmethod m/mutate 'channel/set [{:keys [state ast] :as env} _ params]
  {:action (fn []
             (swap! state assoc :current-channel params))})

(defmethod m/mutate 'user/add [{:keys [state ast] :as env} _ params]
  {:remote ast
   :action (fn []
             (let [{:keys [db/id]} params
                   ident [:user/by-id id]
                   def-chan (first (-> @state :app/channels))]
               (swap! state assoc-in ident params)
               (swap! state assoc :current-channel def-chan :current-user ident)
               (swap! state update :app/users (fnil conj []) ident)
               (swap! state update-in [:channel/by-id (second def-chan)]
                      (fn [chan ident]
                        (update chan :channel/users (fnil conj []) ident))
                      ident))
             {})})

(defmethod m/mutate 'message/add [{:keys [state ast] :as env} _ params]
  {:remote ast
   :action (fn []
             (let [channel-ident (get @state :current-channel)
                   {:keys [db/id]} params
                   ident [:message/by-id id]]
               (swap! state assoc-in ident params)
               (swap! state update-in (conj channel-ident :channel/messages) (fnil conj []) ident))
             {})})

(defmethod m/mutate 'app/subscribe [{:keys [ast]} _ _]
  {:remote ast})
