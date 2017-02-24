(ns app.suite
  (:require-macros
    [untangled-spec.reporters.suite :as ts])
  (:require
    untangled-spec.reporters.impl.suite
    ;; TODO: Require your specs here
    [devtools.core :as devtools]))

(enable-console-print!)

(devtools/enable-feature! :sanity-hints)
(devtools/install!)

(ts/deftest-all-suite app-specs #".*-spec")

(app-specs)

