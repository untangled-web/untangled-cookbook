(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [untangled.ui.forms :as f]
            [taoensso.timbre :as timbre]
            [clojure.java.jdbc :as jdbc]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast)))

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defn om->sql
  "Convert a value coming in from Om into something the SQL database
  is OK with."
  [value]
  (case value
    ::f/none nil
    :female "female"
    :male "male"
    value))

(defn row->sql
  "Convert an incoming Om entity row into something we can use to insert it into the SQL database."
  [row] (into {} (map (fn [[k v]] [k (om->sql v)]) row)))

; Handle the form submission. The submission will look like this:
; { :form/new-entities {[om-table-name om-id] { k-v pairs }}}
; New entities will come as Om tempids, which we will need to remap by returning a :tempids map.
(defmethod apimutate `f/commit-to-entity [{:keys [database]} k {:keys [form/new-entities] :as p}]
  {:action #(let [tmpid (-> new-entities keys first second)
                  row   (-> new-entities vals first row->sql (dissoc :id))]
              (timbre/info (str "Commit: " p " with tmpid " tmpid))
              ; treat the db as a function, this will run the operation provided in a xaction
              (database (fn [c]
                          (let [new-id (-> (jdbc/insert! c :facial_survey row)
                                         first
                                         :id)] ;DB-specific: hsqldb returns a row with the ID that was inserted.
                            {:tempids {tmpid new-id}}))))})


; When the survey is complete, the UI will issue a load on the :final-results key. This generates the response.
(defmethod api-read :final-results [{:keys [database]} k p]
  ; Again using the database as a function that runs a transaction.
  (database (fn [c]
              ; Om requires we wrap our result in a map with :value as the key. Standard Om fare.
              {:value (jdbc/query c ["SELECT (SELECT COUNT(gender) FROM facial_survey WHERE gender = 'male') AS men,
              (SELECT COUNT(gender) FROM facial_survey WHERE gender = 'female') AS women,
              (SELECT COUNT(like_shaving) FROM facial_survey WHERE like_shaving = true) AS like_shaving,
              (SELECT COUNT(beards_sexy) FROM facial_survey WHERE beards_sexy = true) AS beards_sexy
              FROM facial_survey"]
                        {:result-set-fn first})})))
