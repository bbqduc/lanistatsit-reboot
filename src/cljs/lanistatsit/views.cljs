(ns lanistatsit.views
    (:require [re-frame.core :as re-frame]
              [goog.string :as gstring]
              [goog.string.format]))
(defonce lans [{:lan "Lan 1", :wins 10, :losses 8}
               {:lan "Lan 2", :wins 15, :losses 11}])

(defonce herostats [{:name "Hero1", :wins 10, :losses 15}
                    {:name "Hero2", :wins 20, :losses 12}])

(defn percentage-string [percentage]
  (str (* 100 percentage) "%"))

(defn test-statsbox [stats]
  (let [lan (:lan stats)
        wins (:wins stats)
        losses (:losses stats)]
      [:div 
       [:ul 
        [:li lan]
        [:li (gstring/format "Winrate: %.2f%" (percentage-string (/ wins (+ wins losses))))]
        [:li wins "/" losses]
        ]]))

(defn main-panel []
  [:div
    [:ul
     (for [lan lans]
       ^{:key lan} [:li (test-statsbox lan)])]])
