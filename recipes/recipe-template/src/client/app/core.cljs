(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [app.ui :as ui]
    [om.next :as om]))

(def initial-state {})

(defonce app (atom (uc/new-untangled-client
                     :initial-state initial-state
                     :started-callback
                     (fn [{:keys [reconciler]}]
                       ; TODO
                       ))))

