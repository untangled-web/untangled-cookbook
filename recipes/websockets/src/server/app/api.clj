(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(def db
  (atom {:data [{:db/id      :db.temp/datum-1
                 :datum/item "This is datum 1 loaded in intial state."}]}))

(defmulti apimutate om/dispatch)

(defmethod apimutate 'datum/add [env _ params]
  {:action (fn []
             (timbre/info "Received a datum." params)
             (swap! db update :data conj params)
             {})})

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmulti api-read om/dispatch)

(defmethod api-read :data [{:keys [ast query] :as env} dispatch-key params]
  (let [result (get @db dispatch-key)]
    (timbre/info "Data result: " result)
    {:value result}))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast))
  (throw (ex-info "Unexpected api read." {:dispatch-key dispatch-key})))
