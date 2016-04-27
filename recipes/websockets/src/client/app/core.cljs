(ns app.core
  (:require
    app.mutations
    [app.networking.websockets :as ws]
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [om.next :as om]))

(def initial-state {:ui/react-key "abc"})

(defonce app (atom (uc/new-untangled-client
                     :networking (ws/make-websocket-network "/chsk" :global-error-callback (constantly nil))
                     :initial-state initial-state
                     :started-callback
                     (fn [{:keys [reconciler]}]
                       (ws/start! reconciler)
                       ))))
