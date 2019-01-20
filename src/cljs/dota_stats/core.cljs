(ns dota-stats.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))



;; -------------------------
;; State
(defonce app-state
  (r/atom {:search false
           :loading false
           :results []}))

;; HTTP
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

;; -------------------------
;; Page components

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

(defn app []
  (fn []
    [:div#wrap
     [header]
     [body]]))

;; -------------------------
;; Initialize app

(defn init! []
  (r/render [app] (.getElementById js/document "app")))
