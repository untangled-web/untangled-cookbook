(ns app.push-receive
  (:require [app.ui :as ui]
            [om.next :as om]
            [untangled.client.core :refer [refresh]]
            [untangled.websockets.networking :as n]))

(defmethod n/push-received :app/data-update [{:keys [reconciler] :as app} {:keys [data]}]
  (let [state (om/app-state reconciler)
        {:keys [db/id]} data
        ident [:datum/by-id id]]
    (swap! state update :data (fnil conj []) ident)
    (swap! state assoc-in ident data)
    (refresh app)
    ;; (om/merge! reconciler {:data data} [{:data (om/get-query ui/Datum)}])
    ))
