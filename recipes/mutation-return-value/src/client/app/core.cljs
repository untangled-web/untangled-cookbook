(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [app.mutations :as m]
    [om.next :as om]))

(defonce app (atom (uc/new-untangled-client
                     :mutation-merge m/merge-return-value)))

