(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [app.ui :as ui]))

(defonce app (atom (uc/new-untangled-client
                     :started-callback (fn [app]
                                         (df/load app :people ui/Person)))))

