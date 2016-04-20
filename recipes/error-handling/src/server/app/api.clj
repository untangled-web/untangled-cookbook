(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate 'my/mutation [env k params]
  ;; Throw a mutation error for the client to handle
  {:action (fn [] (throw (ex-info "Server error" {:status 401 :body "Unauthorized User"})))})

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast]} _ _]
  (timbre/error "Unrecognized query " (op/ast->expr ast))
  ;; Bug in untangled server requires the error response bodies for reads to be manually encoded as json, will fix soon
  ;; Throwing 403 just to easily see the difference between read and mutate errors in the javascript console
  {:value (throw (ex-info "Unrecognized read" {:status 403 :body "{\"Key\":\"Bad read\"}"}))})
