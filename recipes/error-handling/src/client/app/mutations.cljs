(ns app.mutations
  (:require [untangled.client.mutations :as m]))

(defmethod m/mutate 'my/mutation [env k params]
  ;; Just send it to the server
  {:remote true
   :action (constantly nil)})