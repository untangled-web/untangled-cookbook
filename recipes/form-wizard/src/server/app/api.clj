(ns app.api
  (:require [om.next.server :as om]
            [om.next.impl.parser :as op]
            [untangled.ui.forms :as f]
            [taoensso.timbre :as timbre]
            [clojure.java.jdbc :as jdbc]))

(defmulti apimutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod apimutate :default [e k p]
  (timbre/error "Unrecognized mutation " k))

(defn om->sql [value]
  (case value
    ::f/none nil
    :female "female"
    :male "male"
    value))

(defn row->sql [row] (into {} (map (fn [[k v]] [k (om->sql v)]) row)))

(defmethod apimutate `f/commit-to-entity [{:keys [database]} k {:keys [form/new-entities] :as p}]
  {:action #(let [tmpid (-> new-entities keys first second)
                  row   (-> new-entities vals first row->sql (dissoc :id))]
              (timbre/info (str "Commit: " p " with tmpid " tmpid))
              (database (fn [c]
                          (let [new-id (-> (jdbc/insert! c :facial_survey row)
                                         first
                                         :id)]
                            {:tempids {tmpid new-id}}))))})

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (timbre/error "Unrecognized query " (op/ast->expr ast)))

(defmethod api-read :final-results [{:keys [database]} k p]
  (database (fn [c]
              {:value (jdbc/query c ["SELECT (SELECT COUNT(gender) FROM facial_survey WHERE gender = 'male') AS men,
              (SELECT COUNT(gender) FROM facial_survey WHERE gender = 'female') AS women,
              (SELECT COUNT(like_shaving) FROM facial_survey WHERE like_shaving = true) AS like_shaving,
              (SELECT COUNT(beards_sexy) FROM facial_survey WHERE beards_sexy = true) AS beards_sexy
              FROM facial_survey"]
                        {:result-set-fn first})})))
