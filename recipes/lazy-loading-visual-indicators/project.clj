(defproject untangled/demo "1.0.0"
  :description "Untangled Cookbook Recipe"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[com.datomic/datomic-free "0.9.5206" :exclusions [joda-time]]
                 [com.taoensso/timbre "4.3.1"]
                 [commons-codec "1.10"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.omcljs/om "1.0.0-alpha41"]
                 [binaryage/devtools "0.5.2"]
                 [figwheel-sidecar "0.5.3-1" :exclusions [ring/ring-core joda-time org.clojure/tools.reader]]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [com.google.guava/guava "19.0"]
                 [juxt/dirwatch "0.2.3"]
                 [com.stuartsierra/component "0.3.1"]
                 [bidi "2.0.9"]
                 [prismatic/schema "1.0.3"]
                 [commons-fileupload "1.3.1"]
                 [commons-io "2.4"]
                 [clj-time "0.11.0"]
                 [joda-time "2.8.2"]
                 [navis/untangled-client "0.5.5-SNAPSHOT" :exclusions [cljsjs/react org.omcljs/om]]
                 [navis/untangled-server "0.6.0"]
                 [navis/untangled-spec "0.3.8"]
                 [navis/untangled-datomic "0.4.9" :exclusions [com.datomic/datomic-free org.clojure/tools.cli]]]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :source-paths ["dev/server" "src/server" "checkouts/untangled-client/src"]
  :test-paths ["test/client"]
  :jvm-opts ["-server" "-Xmx1024m" "-Xms512m" "-XX:-OmitStackTraceInFastThrow"]
  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/client" "dev/client" "checkouts/untangled-client/src"]
                :figwheel     true
                :compiler     {:main                 cljs.user
                               :asset-path           "js/compiled/dev"
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/dev"
                               :optimizations        :none
                               :parallel-build       false
                               :verbose              false
                               :recompile-dependents true
                               :source-map-timestamp true}}
               {:id           "test"
                :source-paths ["test/client" "src/client"]
                :figwheel     true
                :compiler     {:main                 app.suite
                               :output-to            "resources/public/js/specs/specs.js"
                               :output-dir           "resources/public/js/compiled/specs"
                               :asset-path           "js/compiled/specs"
                               :recompile-dependents true
                               :optimizations        :none}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:init-ns          user
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
