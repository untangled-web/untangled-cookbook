(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [untangled.client.logging :as log]))

(def initial-state {:ui/react-key "abc"})

(defonce app (atom (uc/new-untangled-client
                     :initial-state initial-state
                     :started-callback
                     (fn [{:keys [reconciler]}]
                       ;; specify a fallback mutation symbol as a named parameter after the component or reconciler and query
                       (df/load-data reconciler [:data]
                         :fallback 'read/error-log))

                     ;; this function is called on *every* network error, regardless of cause
                     :network-error-callback
                     (fn [state status-code error]
                       (log/warn "Global callback:" error " with status code: " status-code)))))
