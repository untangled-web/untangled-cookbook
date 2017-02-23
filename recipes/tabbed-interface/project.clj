(defproject untangled/tabbed-interface "1.0.0"
  :description "Untangled Cookbook Recipe"
  :url ""
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[com.taoensso/timbre "4.3.1"]
                 [commons-codec "1.10"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.omcljs/om "1.0.0-alpha46"]
                 [navis/untangled-client "0.6.0" :exclusions [cljsjs/react org.omcljs/om]]
                 [navis/untangled-server "0.6.0"]
                 [navis/untangled-spec "0.3.9"]
                 [bidi "2.0.9"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :source-paths ["dev/server" "src/server"]
  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"]
  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/client" "dev/client"]
                :figwheel     true
                :compiler     {:main       cljs.user
                               :asset-path "js/compiled/dev"
                               :output-to  "resources/public/js/compiled/app.js"
                               :output-dir "resources/public/js/compiled/dev"
                               :verbose    false}}]}

  :figwheel {:css-dirs    ["resources/public/css"]
             :server-port 3450}

  :profiles {:dev {:source-paths ["dev" "src" "spec"]
                   :repl-options {:init-ns          clj.user
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :env          {:dev true}
                   :dependencies [[figwheel-sidecar "0.5.9"]
                                  [binaryage/devtools "0.5.2"]
                                  [figwheel-sidecar "0.5.7" :exclusions [ring/ring-core org.clojure/tools.analyzer.jvm joda-time org.clojure/tools.reader]]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [juxt/dirwatch "0.2.3"]]}})

