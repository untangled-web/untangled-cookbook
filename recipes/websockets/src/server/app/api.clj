(ns app.api
  (:require [clojure.core.async :refer [chan put!]]
            [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(def db
  (atom {:data [{:db/id      :db.temp/datum-1
                 :datum/item "This is datum 1 loaded in intial state."}]}))

(def push-queue (chan 50))

(defn enqueue-push [topic data & exclusions]
  (put! push-queue {:topic topic :data data :exclusions (set exclusions)}))

(defmulti apimutate om/dispatch)

(defmethod apimutate 'datum/add [{:keys [uid subscription-container] :as env} _ params]
  {:action (fn []
             (swap! db update :data conj params)
             (enqueue-push :app/data-update params uid)
             {})})

(defmethod apimutate 'app/subscribe [{:keys [uid] :as env} _ {:keys [topic] :as params}]
  {:action (fn []
             )})

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmulti api-read om/dispatch)

(defmethod api-read :data [{:keys [ast query] :as env} dispatch-key params]
  (let [result (get @db dispatch-key)]
    {:value result}))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast))
  (throw (ex-info "Unexpected api read." {:dispatch-key dispatch-key})))
