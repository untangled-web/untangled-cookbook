(ns app.components.channel-wrapper
  (:require [com.stuartsierra.component :as component]))

(defrecord ChannelWrapper [subscriptions]
  component/Lifecycle
  (start [component]
    (assoc component :subscriptions (atom {})))
  (stop [component]
    (dissoc component :subscriptions)))

(defn make-channel-wrapper []
  (component/using
    (map->ChannelWrapper {})
    [:channel-server]))
