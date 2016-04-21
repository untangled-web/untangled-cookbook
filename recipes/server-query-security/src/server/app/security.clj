(ns app.security
  (:require [com.rpl.specter :as s]
            [clojure.set :as set]))

; A map from "entry-level" concept/entity to a set of the allowed graph read/navigation keywords
(def whitelist {:person #{:name :address :mate}})

(defn keywords-in-query
  "Returns all of the keywords in the given (arbitrarily nested) query."
  [query] (s/select (s/walker keyword?) query))

; TODO: determine if the user is allowed to start at the given keyword for entity with given ID
(defn authorized-root-entity?
  "Returns true if the given user is allowed to run a query rooted at the entity indicated by the combination of
  query keyword and entity ID.

  TODO: Implement some logic here."
  [user keyword id] true)

(defn authorized-query?
  "Returns true if the given query is ok with respect to the top-level key of the API query (which should have already
  been authorized by `authorized-root-entity?`."
  [query top-key]
  (let [keywords-allowed (get top-key whitelist #{})
        insecure-keywords (set/difference (keywords-in-query query) keywords-allowed)]
    (empty? insecure-keywords)))
