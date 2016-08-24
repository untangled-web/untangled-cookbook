(defproject untangled/demo "1.0.0"
  :description "Component Local State"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.omcljs/om "1.0.0-alpha41"]
                 [navis/untangled-client "0.5.4" :exclusions [cljsjs/react org.omcljs/om]]
                 [navis/untangled-spec "0.3.7-1"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :source-paths ["dev/server"]
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
                               :recompile-dependents true
                               :source-map-timestamp true}}]}

  :figwheel {:css-dirs ["resources/public/css"]}


  :repl-options {:init-ns user}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.5.2"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [figwheel-sidecar "0.5.5" :exclusions [ring/ring-core joda-time org.clojure/tools.reader]]]}})
