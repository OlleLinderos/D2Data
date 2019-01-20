(ns dota-stats.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn find-steam-profile []
  (swap! app-state assoc :search true)
  (swap! app-state assoc :loading true)
  (go (let [response (<! (http/get "https://api.opendota.com/api/search?q=Boat"))]
        (swap! app-state assoc :loading false)
        (swap! app-state assoc-in [:results] (:body response)))))

 
(defn how-long-ago? [date]
  (str (Math/floor
        (/ (- (js/Date.)
              (js/Date. date))
           (* 1000 60 60 24)))
       " days ago"))

