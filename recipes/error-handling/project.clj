(defproject untangled/demo "1.0.0"
  :description "Untangled Cookbook Recipe"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[com.datomic/datomic-free "0.9.5206" :exclusions [joda-time]]
                 [com.taoensso/timbre "4.3.1"]
                 [commons-codec "1.10"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.omcljs/om "1.0.0-alpha32"]
                 [binaryage/devtools "0.5.2"  :exclusions [environ]]
                 [figwheel-sidecar "0.5.0-3" :exclusions [ring/ring-core joda-time org.clojure/tools.reader]]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [juxt/dirwatch "0.2.3"]
                 [navis/untangled-client "0.4.7-SNAPSHOT" :exclusions [cljsjs/react org.omcljs/om]]
                 [navis/untangled-server "0.4.5"]
                 [navis/untangled-datomic "0.4.4" :exclusions [com.datomic/datomic-free org.clojure/tools.cli]]]

  :plugins [[lein-cljsbuild "1.1.2"] [lein-environ "1.0.0"]]

  :source-paths ["dev/server" "src/server"]
  :jvm-opts ["-server" "-Xmx1024m" "-Xms512m" "-XX:-OmitStackTraceInFastThrow"]
  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/client" "dev/client"]
                :figwheel     true
                :compiler     {:main                 cljs.user
                               :asset-path           "js/compiled/dev"
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/dev"
                               :optimizations        :none
                               :parallel-build       false
                               :verbose              false
                               :recompile-dependents true
                               :source-map-timestamp true}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:init-ns          user
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
)
