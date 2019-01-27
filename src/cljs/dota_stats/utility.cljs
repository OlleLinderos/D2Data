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

(defn calc-winrate [results]
  (into []
   (let [running-win-totals
         (->> results
              (reductions (fn [acc {:keys [result]}]
                            (if (pos? result) (inc acc) acc))
                          0)
              (rest))]
     (sequence
      (comp
       (map-indexed (fn [round win-total] [win-total (inc round)]))
       (map (partial apply /))
       (map #(* 100 %))
       (map float))
      running-win-totals))))
