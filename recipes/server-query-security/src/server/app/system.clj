(ns app.system
  (:require
    [untangled.server.core :as core]
    [app.authentication :as auth]
    [app.api :as api]
    [om.next.server :as om]
    [taoensso.timbre :as timbre]
    [om.next.impl.parser :as op]))

(defn logging-mutate [env k params]
  (timbre/info "Mutation Request: " k)
  (api/apimutate env k params))

(defn logging-query [{:keys [ast] :as env} k params]
  (timbre/info "Query: " (op/ast->expr ast))
  (api/api-read env k params))

(defn make-system []
  (core/make-untangled-server
    :config-path "config/recipe.edn"
    :parser (om/parser {:read logging-query :mutate logging-mutate})
    ; Inject the authentication bit
    :parser-injections #{:authentication}
    :components {
                 ; The auth hook puts itself into the Ring pipeline
                 :auth-hook      (auth/make-authentication)
                 ; The authentication bit is for checking reads
                 :authentication (auth/make-authorizer)}))
