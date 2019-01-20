(ns dota-stats.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [reagent.core :as r]
            [reagent.session :as session]))


;; -------------------------
;; All state goes here

(defonce app-state
  (r/atom {:search false
           :loading false
           :results []}))


;; -------------------------
;; Fetch data from opendota API

(defn find-steam-profile [query]
  (swap! app-state assoc :loading true)
  (swap! app-state assoc :search true)
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/search?q=" query)))]
        (prn (:body response))
        (swap! app-state assoc :loading false)
        (swap! app-state assoc-in [:results] (:body response)))))


;; -------------------------
;; Utility functions

(defn how-long-ago? [date]
  (Math/floor
   (/ (- (js/Date.)
         (js/Date. date))
      (* 1000 60 60 24)))) 

(defn sort-by-recency [vec]
  (reverse (sort-by :last_match_time vec)))

;; -------------------------
;; Components

(defn search-form []
  (fn []
    [:div.search-form
     [:h2 "Search for profile"]
     [:form
      [:input {:type "text" :placeholder "Your Steam username..."}]
      [:input {:type "button" :value "Search" :on-click #(find-steam-profile "Boat")}]]]))

(defn search-results []
  (fn []
    (if (true? (get @app-state :loading))
      [:p "Loading..."]
      [:div.search-results
       [:h2 "Select your account"]
       [:ul.results-list
        (for [user (sort-by-recency (:results @app-state))]
          [:li.user {:key (user :account_id)}
           [:img.user-img {:src (user :avatarfull)}]
           [:div.user-info
            [:p (user :personaname)]
            [:p (str (how-long-ago? (user :last_match_time)) " days ago")]]])]])))

(defn header []
  (fn []
    [:header.app-head
     [:h1 "Dota Graph"]]))

(defn body []
  (fn []
    [:div.app-body
     (if (false? (get @app-state :search))
       [search-form]
       [search-results])]))

(defn app []
  (fn []
    [:div#wrap
     [header]
     [body]]))


;; -------------------------
;; Initialize the app

(defn init! []
  (r/render [app] (.getElementById js/document "app")))
