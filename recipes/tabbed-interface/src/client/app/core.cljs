(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [app.ui :as ui]))

(defonce app (atom (uc/new-untangled-client
                     :started-callback (fn [app]
                                         ;; construction can only initialize the "default" tab...add the rest in like this:
                                         (uc/merge-state! app ui/TabUnion (uc/initial-state ui/SettingsTab nil))))))

