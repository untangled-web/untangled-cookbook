(ns app.system
  (:require
    [app.api :as api]
    [app.components.web-sockets :as ws]
    [om.next.impl.parser :as op]
    [om.next.server :as om]
    [taoensso.timbre :as timbre]
    [untangled.server.core :as core]))

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
    :parser-injections #{}
    :components {:web-socket (ws/make-web-socket-registry)}
    :extra-routes {:routes   ["" {["/chsk"] :web-socket-location}]
                   :handlers {:web-socket-location ws/route-handlers}}))
