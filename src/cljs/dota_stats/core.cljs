(ns dota-stats.core
  (:require [dota-stats.data]
            [reagent.core :as r]
            [reagent.session :as session]))

;; -------------------------
;; All state goes here

(defonce app-state
  (r/atom {:search false
           :loading false
           :results []}))

;; -------------------------
;; Components

(defn search-form []
  (fn []
    [:div.search-form
     [:h2 "Search for profile"]
     [:form
      [:input {:type "text" :placeholder "Your Steam username..."}]
      [:input {:type "button" :value "Search" :on-click #(find-steam-profile)}]]]))

(defn search-results []
  (fn []
    (if (true? (get @app-state :loading))
      [:p "Loading..."]
      [:div.search-results
       [:h2 "Select your account"]
       [:ul.results-list
        (for [user (:results @app-state)]
          [:li.user
           [:img.user-img {:src (user :avatarfull)}]
           [:div.user-info
            [:p (user :personaname)]
            [:p (how-long-ago? (user :last_match_time))]]])]])))

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
