(ns dota-stats.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [reagent.core :as r]
            [reagent.session :as session]))


;; -------------------------
;; All state goes here

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

;; -------------------------
;; Fetch data from opendota API

(defn get-steam-profiles [query]
  (swap! app-state assoc :state "loading")
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/search?q=" query)))]
        (swap! app-state assoc :state "users")
        (swap! app-state assoc-in [:users] (:body response)))))

(defn get-matches [query]
  (swap! app-state assoc :state "loading")
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/players/" query "/matches")))]
        (swap! app-state assoc :state "matches")
        (swap! app-state assoc-in [:matches] (:body response)))))

(defn get-won-matches [query]
  (go (let [response (<! (http/get (str "https://api.opendota.com/api/players/" query "/matches?win=1")))]
        (swap! app-state assoc-in [:match-wins] (:body response)))))

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

(defn username-input [value]
  [:input {:name "username"
           :placeholder "Enter your Steam username"
           :required "required"
           :type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn user-search-form []
  (let [steam-username (r/atom "")]
    (fn []
      [:div.search-form
       [:h2 "Find your profile"]
       [:form
        [username-input steam-username]
        [:input {:type "button" :value "Search" :on-click #(get-steam-profiles @steam-username)}]]])))

(defn user-search-results []
  (fn []
    [:div.search-results
     [:h2 "Select your account"]
     [:ul.results-list
      (for [user (sort-by-recency (:users @app-state))]
        ^{:key user} [:li.user {:on-click #(get-matches (user :account_id))}
                      [:img.user-img {:src (user :avatarfull)}]
                      [:div.user-info
                       [:p (user :personaname)]
                       [:p (str (how-long-ago? (user :last_match_time)) " days ago")]]])]]))

(defn matches-component []
  (fn []
    [:ol
     (for [match (:matches @app-state)]
       ^{:key match} [:li (str match)])]))

(defn loading-component []
  [:p "Loading"])

(defn header []
  (fn []
    [:header.app-head
     [:h1 {:on-click #(reset-state)} "D2Data"]]))

(defn body []
  (fn []
    [:div.app-body
     (case (get @app-state :state)
       "search" [user-search-form]
       "users" [user-search-results]
       "matches" [matches-component]
       "loading" [loading-component])]))

(defn app []
  (fn []
    [:div#wrap
     [header]
     [body]]))


;; -------------------------
;; Initialize the app

(defn init! []
  (r/render [app] (.getElementById js/document "app")))
