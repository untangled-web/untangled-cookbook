(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [hugsql.core :as hugsql]
            [taoensso.timbre :as timbre]
            [clojure.set :as set]))

(declare all-people get-person)

(hugsql/def-db-fns "app/people.sql")

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast)))

(defmethod api-read :people [{:keys [ast query db] :as env} dispatch-key params]
  (let [c (:connection db)
        people (all-people {:connection c} {})
        result (mapv #(set/rename-keys % {:id :db/id :name :person/name :address :person/address :age :person/age}) people)]
    {:value result}))
