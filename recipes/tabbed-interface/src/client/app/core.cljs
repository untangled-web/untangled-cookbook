(ns app.core
  (:require
    app.mutations
    [untangled.client.core :as uc]
    [untangled.i18n :refer-macros [tr trf]]
    [untangled.client.data-fetch :as df]
    [om.next :as om]))

(def initial-state
  (atom {
         ; These are database tables. We need this structure for the ident to work properly (it looks up something
         ; in a table when followed in the db). Of course, the tabs sub-structure can be completely different
         ; since the UI query can be different for each. That's the point. We *do* need data *inside* these objects
         ; that can be pulled by the ident function of the TabUnion so it can generate the proper ident on the fly.
         ; Thus, the :id and :which-tab fields.
         :main        {:tab {:id :tab :which-tab :main :main-content "Main tab"}}
         :settings    {:tab {:id :tab :which-tab :settings :settings-content "Settings tab"}}

         ; The following is the top-level key that can be queried to find which tab to show.
         ; The first element of the ident is the "type" of tab, and will choose the query from
         ; the union in the UI (TabUnion)
         :current-tab [:main :tab]}))

(defonce app (atom (uc/new-untangled-client :initial-state initial-state)))

