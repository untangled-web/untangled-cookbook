(ns app.mutations
  (:require [untangled.client.mutations :as m]))

(defmethod m/mutate 'set-to-tony [{:keys [state]} sym params]
  {:action (fn [] (swap! state assoc :value "Tony"))})

(defmethod m/mutate 'nav/settings [{:keys [state]} sym params]
  {:action (fn [] (swap! state assoc :panes [:settings :singleton]))})

(defmethod m/mutate 'nav/main [{:keys [state]} sym params]
  {:action (fn [] (swap! state assoc :panes [:main :singleton]))})

