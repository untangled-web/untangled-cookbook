(defproject untangled/server-sql "1.0.0"
  :description "Untangled Cookbook Recipe - SQL"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[com.taoensso/timbre "4.3.1"]
                 [org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.omcljs/om "1.0.0-alpha47"]
                 [binaryage/devtools "0.5.2"]
                 [figwheel-sidecar "0.5.9"]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/core.async "0.2.391"]
                 [navis/untangled-client "0.6.0"]
                 [navis/untangled-server "0.6.2"]
                 [com.h2database/h2 "1.4.193"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.flywaydb/flyway-core "4.0.3"]
                 [bidi "2.0.9"]
                 [navis/untangled-spec "0.3.9"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :source-paths ["dev/server" "src/server"]
  :test-paths ["test/client"]
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
