(ns dota-stats.core
  (:require [dota-stats.state :as state]
            [dota-stats.utility :as util]
            [dota-stats.data :as data]
            [reagent.core :as r]
            [reagent.session :as session]
            [cljsjs.chartjs]))

(defn username-input [value]
  [:input {:name "username"
           :placeholder "Enter your Steam username"
           :required "required"
           :type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))
           :on-key-press (fn [e]
                           (if (= (.-key e) "Enter")
                             (do
                               (.preventDefault e)
                               (data/get-steam-profiles @value))))}])

(defn user-search-form []
  (let [steam-username (r/atom "")]
    (fn []
      [:div.search-form
       [:h2 "Find your profile"]
       [:form
        [username-input steam-username]
        [:input {:type "button"
                 :value "Search"
                 :on-click #(data/get-steam-profiles @steam-username)}]]])))

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

(defn setup-winrate-chart []
  (let [context (.getContext (.getElementById js/document "winrate-chart") "2d")
        chart-data {:type "line"
                    :options {:responsive true
                              :scales {:yAxes [{:display true
                                                :ticks {:min 0
                                                        :max 100
                                                        :stepSize 10}}]
                                       :xAxes [{:display false
                                                :unitStepSize 1
                                                :ticks {:beginAtZero true
                                                        :autoSkip false}}]}
                              :legend {:display false}}
                    :data {:labels (map #(str "Game #" (+ 1 %))
                                        (vec (range (count (util/calc-winrate
                                                            (util/merge-results
                                                             (:wins @state/app-state)
                                                             (:losses @state/app-state)))))))
                           :datasets [{:fill false
                                       :pointRadius 0
                                       :pointHitRadius 5
                                       :pointHoverBackgroundColor "#fff"
                                       :borderColor "#FF9900"
                                       :backgroundColor "#FF9900"
                                       :label "Winrate"
                                       :data (util/calc-winrate
                                              (util/merge-results
                                               (:wins @state/app-state) (:losses @state/app-state)))}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn winrate-chart []
  (r/create-class
   {:component-did-mount #(setup-winrate-chart)
    :reagent-render (fn []
                      [:canvas {:id "winrate-chart" :width "900" :height "500"}])}))

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
       "winrate" [winrate-chart]
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
