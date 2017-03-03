(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast)))

(defmethod api-read :ui [{:keys [ast query] :as env} dispatch-key params]
  (let [component (second (:key ast))]
    (case component
      :panel {:value {:child {:db/id 5 :child/label "Child"}}}
      :child {:value {:items [{:db/id 1 :item/label "A"} {:db/id 2 :item/label "B"}]}}
      nil)))

(defmethod api-read :items/by-id [{:keys [ query-root] :as env} _ params]
  (let [id (second query-root)]
    (timbre/info "Item query for " id)
    {:value {:db/id id :item/label (str "Refreshed Label " (rand-int 100))}}))
