(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [om.next :as om]))

(def initial-state
  (atom {:main        {:tab {:id :tab :which-tab :main :main-content "Main tab"}}
         :settings    {:tab {:id :tab :which-tab :settings :settings-content "Settings tab"}}
         :current-tab [:main :tab]}))

(defonce app (atom (uc/new-untangled-client :initial-state initial-state)))

