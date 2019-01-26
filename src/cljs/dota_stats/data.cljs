(ns dota-stats.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [dota-stats.state :as state]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn get-steam-profiles [query]
  (swap! state/app-state assoc :state "loading")
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/search?q=" query)))]
        (swap! state/app-state assoc :state "users")
        (swap! state/app-state assoc-in [:users] (:body response)))))

(defn get-matches [query]
  (swap! state/app-state assoc :state "loading")
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/players/" query "/matches")))]
        (swap! state/app-state assoc :state "matches")
        (swap! state/app-state assoc-in [:matches] (:body response)))))

(defn get-won-matches [query]
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/players/" query "/matches?win=1")))]
        (swap! state/app-state assoc-in [:match-wins] (:body response)))))
