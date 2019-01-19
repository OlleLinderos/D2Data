(ns dota-stats.prod
  (:require [dota-stats.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
