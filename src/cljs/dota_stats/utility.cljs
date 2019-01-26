(ns dota-stats.utility)

(defn how-long-ago? [date]
  (Math/floor
   (/ (- (js/Date.)
         (js/Date. date))
      (* 1000 60 60 24)))) 

(defn sort-by-recency [vec]
  (reverse (sort-by :last_match_time vec)))

