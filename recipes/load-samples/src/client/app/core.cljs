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
                                         ; Make sure you're running the app from the real server port (not fighweel).
                                         ; This is a sample of loading a list of people into a given target, including
                                         ; use of params. The generated network query will result in params
                                         ; appearing in the server-side query, and :people will be the dispatch
                                         ; key. The subquery will also be available (from Person)
                                         (df/load app :people ui/Person {:target [:lists/by-type :enemies :people]
                                                                         :params {:kind :enemy}})
                                         (df/load app :people ui/Person {:target [:lists/by-type :friends :people]
                                                                         :params {:kind :friend}})))))

