(ns user
  (:require
    [com.stuartsierra.component :as component]
    [figwheel-sidecar.system :as sys]
    [clojure.pprint :refer (pprint)]
    [clojure.stacktrace :refer (print-stack-trace)]))

;;FIGWHEEL
(def figwheel-config (sys/fetch-config))
(def figwheel (atom nil))

(defn start-figwheel
  "Start Figwheel on the given builds, or defaults to build-ids in `figwheel-config`."
  ([]
   (let [props (System/getProperties)
         all-builds (->> figwheel-config :data :all-builds (mapv :id))]
     (start-figwheel (keys (select-keys props all-builds)))))
  ([build-ids]
   (let [default-build-ids (-> figwheel-config :data :build-ids)
         build-ids (if (empty? build-ids) default-build-ids build-ids)
         preferred-config (assoc-in figwheel-config [:data :build-ids] build-ids)]
     (reset! figwheel (component/system-map :figwheel-system (sys/figwheel-system preferred-config)))
     (println "STARTING FIGWHEEL ON BUILDS: " build-ids)
     (swap! figwheel component/start)
     (sys/cljs-repl (:figwheel-system @figwheel)))))
