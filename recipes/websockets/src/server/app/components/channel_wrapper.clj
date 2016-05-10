(ns app.components.channel-wrapper
  (:require [app.api :as api]
            [com.stuartsierra.component :as component]
            [untangled.websockets.protocols :refer [WSListener client-dropped client-added add-listener remove-listener push]]))

(defrecord ChannelWrapper [channel-server subscriptions]
  WSListener
  (client-dropped [this ws-net cid]
    (swap! subscriptions update :general (fnil disj #{}) cid)
    (api/remove-user :db.temp/channel-1 cid)
    (api/notify-others ws-net cid :user/left {:db/id cid}))
  (client-added [this ws-net cid]
    (swap! subscriptions update :general (fnil conj #{}) cid))

  component/Lifecycle
  (start [component]
    (let [component (assoc component
                      :subscriptions (atom {}))]
      (add-listener channel-server component)
      component))
  (stop [component]
    (remove-listener channel-server component)
    (dissoc component :subscriptions :kill-chan)))

(defn make-channel-wrapper []
  (component/using
    (map->ChannelWrapper {})
    [:channel-server]))
