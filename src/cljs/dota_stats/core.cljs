(ns dota-stats.core
  (:require [dota-stats.state :as state]
            [dota-stats.utility :as util]
            [dota-stats.data :as data]
            [reagent.core :as r]
            [reagent.session :as session]))

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
        [:input {:type "button" :value "Search" :on-click #(data/get-steam-profiles @steam-username)}]]])))

(defn user-search-results []
  (fn []
    [:div.search-results
     [:h2 "Select your account"]
     [:ul.results-list
      (for [user (util/sort-by-recency (:users @state/app-state))]
        ^{:key user} [:li.user {:on-click #(data/get-matches (user :account_id))}
                      [:img.user-img {:src (user :avatarfull)}]
                      [:div.user-info
                       [:p (user :personaname)]
                       [:p (str (util/how-long-ago? (user :last_match_time)) " days ago")]]])]]))

(defn matches-component []
  (fn []
    [:ol
     (for [match (:matches @state/app-state)]
       ^{:key match} [:li (str match)])]))

(defn loading-component []
  [:p "Loading"])

(defn header []
  (fn []
    [:header.app-head
     [:h1 {:on-click #(state/reset-state)} "D2Data"]]))

(defn body []
  (fn []
    [:div.app-body
     (case (get @state/app-state :state)
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
