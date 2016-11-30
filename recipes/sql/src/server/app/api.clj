(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [hugsql.core :as hugsql]
            [taoensso.timbre :as timbre]
            [clojure.set :as set]))

(declare all-people next-person-id insert-person)

(hugsql/def-db-fns "app/people.sql")

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod apimutate 'app/add-person [{:keys [db] :as env} _ {:keys [id age name]}]
  {:action (fn []
             (let [real-id (-> (next-person-id db) :id)]
               (timbre/info "Inserting person with new id " real-id)
               (insert-person db {:id real-id :name name :age age})
               {:tempids {id real-id}}))})

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query on dk " dispatch-key " with ast " (op/ast->expr ast)))

(defmethod api-read :people [{:keys [ast query db] :as env} dispatch-key params]
  (let [people (all-people db {})
        result (mapv #(set/rename-keys % {:id :db/id :name :person/name :age :person/age}) people)]
    {:value result}))
