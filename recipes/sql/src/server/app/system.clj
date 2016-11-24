(ns app.system
  (:require
    [untangled.server.core :as core]
    [app.api :as api]
    [om.next.server :as om]
    [taoensso.timbre :as timbre]
    [om.next.impl.parser :as op]
    [com.stuartsierra.component :as component])
  (:import (java.sql Connection DriverManager)
           (org.flywaydb.core Flyway)))

(defrecord SQLDatabase [^Connection connection]
  component/Lifecycle
  (start [this]
    (try
      (Class/forName "org.h2.Driver")
      (let [url "jdbc:h2:mem:default"
            c (DriverManager/getConnection url)
            flyway ^Flyway (Flyway.)]
        (.setDataSource flyway url "" "" nil)
        (.migrate flyway)
        (assoc this :connection c))
      (catch Exception e
        (timbre/error "Failed to start database " e)
        this)))
  (stop [this]
    (when connection
      (.close connection))
    (dissoc this :connection)))

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
    :parser-injections #{:db}
    :components {:db (map->SQLDatabase {})}))
