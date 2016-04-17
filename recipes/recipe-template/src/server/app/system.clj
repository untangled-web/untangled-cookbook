(ns app.system
  (:require
    [untangled.server.core :as core]
    [app.api :as api]
    [om.next.server :as om]
    [taoensso.timbre :as timbre]))

(defn logging-mutate [env k params]
  (timbre/info "Mutation Request: " k)
  (api/apimutate env k params))

(defn logging-query [{:keys [ast]} k params]
  (timbre/info "Query: " (op/ast->expr ast))
  (api/api-read env k params))

; build the server
(defn make-system []
  (core/make-untangled-server
    :config-path "recipe.edn"
    :parser (om/parser {:read logging-query :mutate logging-mutate})
    :parser-injections #{}
    :components {}))
