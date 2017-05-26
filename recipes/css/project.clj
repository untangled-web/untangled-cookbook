(defproject untangled/demo "1.0.0"
  :description "Untangled Cookbook Recipe"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.omcljs/om "1.0.0-alpha48"]
                 [binaryage/devtools "0.9.4"]
                 [figwheel-sidecar "0.5.9"]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [garden "1.3.2"]
                 [untangled/om-css "1.0.2"]
                 [navis/untangled-client "0.6.0" :exclusions [cljsjs/react org.omcljs/om]]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src/client"]
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
               {:id           "release"
                :source-paths ["src/client"]
                :compiler     {:main          app.main
                               :output-to     "resources/public/js/release.js"
                               :output-dir    "resources/public/js/release"
                               :asset-path    "js/release"
                               :optimizations :advanced}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:init-ns          user
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles {:dev {:source-paths ["dev/server" "src/client"]}}
  )
