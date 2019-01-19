(ns ^:figwheel-no-load dota-stats.dev
  (:require
    [dota-stats.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
