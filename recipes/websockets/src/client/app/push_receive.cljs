(ns app.push-receive
  (:require [app.ui :as ui]
            [om.next :as om]
            [untangled.client.core :refer [refresh]]
            [untangled.client.data-fetch :as df]
            [untangled.websockets.networking :as n]
            [untangled.dom :as udom]))

(defmethod n/push-received :user/left [{:keys [reconciler] :as app} {:keys [msg]}]
  (om/transact! reconciler `[(push/user-left ~{:msg msg})]))

(defmethod n/push-received :user/new [{:keys [reconciler] :as app} {:keys [msg]}]
  (om/transact! reconciler `[(push/user-new ~{:msg msg})]))

(defmethod n/push-received :message/new [{:keys [reconciler] :as app} {:keys [msg]}]
  (om/transact! reconciler `[(push/message-new ~{:msg msg})]))
