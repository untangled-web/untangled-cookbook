(ns app.main
  (:require
    [app.core :refer [app]]
    [untangled.client.core :as core]
    [om-css.core :as css]
    [app.ui :as ui]))

(css/upsert-css "app_style" ui/Root)

(reset! app (core/mount @app ui/Root "app"))
