(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [om.next :as om]
    [app.ui :as ui]))

(defonce app (atom (uc/new-untangled-client
                     :started-callback (fn [app]
                                         ; Sample of loading a list of people into a given target, including
                                         ; use of params
                                         (df/load app :people ui/Person {:target [:lists/by-type :enemies :people]
                                                                         :params {:kind :enemy}})
                                         (df/load app :people ui/Person {:target [:lists/by-type :friends :people]
                                                                         :params {:kind :friend}})))))

