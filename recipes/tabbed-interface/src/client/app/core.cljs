(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]))

(defonce app (atom (uc/new-untangled-client)))

