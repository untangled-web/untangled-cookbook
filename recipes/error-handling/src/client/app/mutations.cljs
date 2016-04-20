(ns app.mutations
  (:require [untangled.client.mutations :as m]
            [untangled.dom :as d]
            [untangled.client.logging :as log]))

(defmethod m/mutate 'my/mutation [env k params]
  ;; Just send the mutation to the server, which will return an error
  {:remote true :action (constantly nil)})

;; an :error key is injected into the fallback mutation's params argument
(defmethod m/mutate 'button/disable [{:keys [state]} k {:keys [error] :as params}]
  {:action (fn []
             (log/warn "Mutation specific fallback -- disabling button")
             (swap! state assoc-in [:child :ui/button-disabled] true))})

(defmethod m/mutate 'read/error-log [env k {:keys [error]}]
  {:action (fn [] (log/warn "Read specific fallback: " error))})
