(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [app.security :as sec]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(def pretend-database {:person {:id 42 :name "Joe" :address "111 Nowhere" :cc-number "1234-4444-5555-2222"}})

(defmethod api-read :person [{:keys [ast query] :as env} dispatch-key params]
  (let [enforce-security? true] ; Flip this and reset server to see client result
    (when enforce-security?
      (or (and
            ;; of course, the params would be derived from the request/headers/etc.
            (sec/authorized-root-entity? :some-user-data :person 42)
            (sec/authorized-query? query :person))
          (throw ex-info "Unauthorized query!" {:query (op/ast->expr ast)})))
    ;; Emulate a datomic pull kind of operation...
    {:value (select-keys pretend-database query)}))

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " dispatch-key (op/ast->expr ast)))
