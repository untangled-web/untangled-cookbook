(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [untangled.ui.forms :as f]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod apimutate `f/commit-to-entity [e k {:keys [form/new-entities] :as p}]
  {:action #(let [tmpid (-> new-entities keys first second)]
              (timbre/error (str "Commit: " p " with tmpid " tmpid))
              {:tempids {tmpid 42}})})

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast)))
