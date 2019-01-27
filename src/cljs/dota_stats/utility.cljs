(ns dota-stats.utility
  (:require [dota-stats.state :as state]
            [reagent.core :as r]))

(defn how-long-ago? [date]
  (Math/floor
   (/ (- (js/Date.)
         (js/Date. date))
      (* 1000 60 60 24)))) 

(defn sort-by-recency [vec]
  (reverse (sort-by :last_match_time vec)))

(defn merge-results [wins losses]
  (sort-by :match_id (concat wins losses)))

(defn calc-winrate [matches]
  (loop [wins 0
         i 1]
    (if (= i (count matches))
      (* (/ wins i) 100)
      (recur
       (if (eq (:result match) 0)
         (prn (* (/ wins i) 100)))
       (inc i)))))
