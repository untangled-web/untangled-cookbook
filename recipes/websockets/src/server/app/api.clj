(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(def initial-db
  {:data [{:db/id      :db.temp/datum-1
           :datum/item "This is datum 1 loaded in intial state."}]})

(defmulti apimutate om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmulti api-read om/dispatch)

(defmethod api-read :data [{:keys [ast query] :as env} dispatch-key params]
  (let [result (get initial-db dispatch-key)]
    (timbre/info "Data result: " result)
    {:value result}))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/info "Dispatch key: " dispatch-key)
  (timbre/error "Unrecognized query " (op/ast->expr ast)))
