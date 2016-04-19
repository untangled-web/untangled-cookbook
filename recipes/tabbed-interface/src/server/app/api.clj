(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
    (timbre/error "Unrecognized query on dispatch key " dispatch-key (op/ast->expr ast)))

(defmethod api-read :tab-data-query [env dispatch-key params]
  {:value {:text "Value from SERVEr"}})
