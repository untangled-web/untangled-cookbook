(ns app.system
  (:require [app.api :as api]
            [app.components.channel-listener :as cl]
            [om.next.impl.parser :as op]
            [om.next.server :as om]
            [taoensso.timbre :as timbre]
            [untangled.server.core :as core]
            [untangled.websockets.components.channel-server :as cs]))

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
    :components {:channel-server  (cs/make-channel-server)
                 :channel-listener (cl/make-channel-listener)}
    :extra-routes {:routes   ["" {["/chsk"] :web-socket}]
                   :handlers {:web-socket cs/route-handlers}}))
