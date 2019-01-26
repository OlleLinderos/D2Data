(ns dota-stats.state
  (:require [reagent.core :as r]))

(defonce app-state
  (r/atom {:state "search"
           :users []
           :matches []
           :match-wins []}))

(defn reset-state []
  (swap! app-state assoc :state "search")
  (swap! app-state assoc :users [])
  (swap! app-state assoc :matches [])
  (swap! app-state assoc :match-wins []))

