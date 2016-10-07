(ns app.main
  (:require
    [app.core :refer [app]]
    [untangled.client.core :as core]
    [app.ui :as root]))

(swap! app core/mount root/Root "app")
