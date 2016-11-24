(ns app.mutations
  (:require [untangled.client.mutations :as m]))

(defmulti merge-return-value (fn [state sym return-value] sym))

; Do all of the work on the server.
(defmethod m/mutate 'crank-it-up [env k params] {:remote true})

(defmethod merge-return-value 'crank-it-up [state _ {:keys [value]}]
  (assoc-in state [:child/by-id 0 :volume] value))