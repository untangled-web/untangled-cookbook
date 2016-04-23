(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [app.authentication :as auth]
            [taoensso.timbre :as timbre]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(def pretend-database {:person {:id 42 :name "Joe" :address "111 Nowhere" :cc-number "1234-4444-5555-2222"}})

(defmethod api-read :person [{:keys [ast authentication request query] :as env} dispatch-key params]
  (let [enforce-security? true
        ; The user is added by the authentication hook into Ring
        user (:user request)]
    (when enforce-security?
      (or (and
            ;; of course, the params would be derived from the request/headers/etc.
            (auth/can-access-entity? authentication user :person 42)
            (auth/authorized-query? authentication user :person query))
          (throw (ex-info "Unauthorized query!" {:status 401 :body {:query query}}))))
    ;; Emulate a datomic pull kind of operation...
    {:value (select-keys (get pretend-database :person) query)}))

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " dispatch-key (op/ast->expr ast)))
