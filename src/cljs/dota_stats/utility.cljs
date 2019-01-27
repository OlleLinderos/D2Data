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
  (if (= 0 total wins) 0
      (* (/ wins total) 100)))

(defn calc-winrate [matches]
  (let [data (r/atom [])]
    (loop [wins 0
           total 0]
      (if (= total (count matches))
        @data
        (recur (if (= (get (nth matches total) :result) 1)
                 (inc wins))
               (do
                  (swap! data conj (to-percentage wins total))
                  (inc total)))))))

(calc-winrate [{:result 0} {:result 1} {:result 0} {:result 1} {:result 1}])
