(ns dota-stats.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]))

;; -------------------------
;; Page components

(defn header []
  (fn []
    [:header
     [:h1 "Dota 2 Statistics"]]))

(defn search-form []
  (fn []
    [:div 
     [:h2 "Enter your Dota 2 Username"]
     [:form
      [:input {:type "text" :placeholder "Your username..."}]
      [:input {:type "submit" :value "Search"}]]]))

(defn app []
  (fn []
    [:div.main
     [header]
     [search-form]]))

;; -------------------------
;; Initialize app

(defn init! []
  (reagent/render [app] (.getElementById js/document "app")))
