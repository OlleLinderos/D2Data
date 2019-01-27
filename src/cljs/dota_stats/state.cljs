(ns dota-stats.state
  (:require [reagent.core :as r]))

(defonce app-state
  (r/atom {:state "search"
           :users []
           :wins []
           :losses []}))

(defn reset-state []
  (swap! app-state assoc :state "search")
  (swap! app-state assoc :users [])
  (swap! app-state assoc :wins [])
  (swap! app-state assoc :losses []))
