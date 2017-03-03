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
      :panel {:value {:child {:label "Child"}}}
      :child {:value {:items [{:label "A"} {:label "B"}]}}
      nil)))

(defmethod api-read :item [{:keys [ query-root] :as env} _ params]
  (let [label (second query-root)]
    (timbre/info "Item query for " label)
    {:value {:label label}}))
