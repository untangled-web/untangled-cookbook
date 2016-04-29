(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [om.next :as om]))

(def initial-state (atom {:ui/react-key "abc"
                          :children     [[:child/by-id 1] [:child/by-id 2] [:child/by-id 3]]
                          :child/by-id  {1 {:id 1 :name "A"}
                                         2 {:id 2 :name "B"}
                                         3 {:id 3 :name "C"}}}))

(defonce app (atom (uc/new-untangled-client :initial-state initial-state)))

