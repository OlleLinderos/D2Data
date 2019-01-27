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

(defn to-percentage [wins total]
  (* (/ wins total) 100))

(defn calc-winrate [matches]
  (loop [wins 0
         i 1]
    (if (= i (count matches))
      (prn "hey")
      (recur
       (do
         (inc wins)
         (conj (to-percentage wins i) data)
       (inc i))))))

(calc-winrate [{:result 0} {:result 1} {:result 0} {:result 1} {:result 1}])
