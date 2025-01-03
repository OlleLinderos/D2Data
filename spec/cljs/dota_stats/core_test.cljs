(ns dota-stats.core-spec
  (:require-macros [speclj.core :refer [describe it should= should should-not]])
  (:require [speclj.core]
            [reagent.core :as reagent :refer [atom]]
            [dota-stats.core :as rc]))


(def isClient (not (nil? (try (.-document js/window)
                              (catch js/Object e nil)))))

(def rflush reagent/flush)

(defn add-test-div [name]
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient
    (let [div (add-test-div "_testreagent")]
      (let [comp (reagent/render-component comp div #(f comp div))]
        (reagent/unmount-component-at-node div)
        (reagent/flush)
        (.removeChild (.-body js/document) div)))))


(defn found-in [re div]
  (let [res (.-innerHTML div)]
    (if (re-find re res)
      true
      (do (println "Not found: " res)
          false))))


(describe "test app init"
  (it "contains an app'"
      (with-mounted-component (rc/app)
        (fn [c div]
          (should (found-in #"D2Data" div))))))

