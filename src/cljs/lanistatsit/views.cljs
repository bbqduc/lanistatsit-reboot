(ns lanistatsit.views
    (:require [re-frame.core :as re-frame]
              [goog.string :as gstring]
              [goog.string.format]
              ))

(defonce lans [{:lan "Lan 1", :wins 10, :losses 8}
               {:lan "Lan 2", :wins 15, :losses 11}])

(defn herostats-tablerow [hero statkeys]
  [:tr
  (for [key statkeys]
      [:td (get hero key)])
   ])

(defn herostats-header-cell [key]
  [:td 
   {:on-click #(re-frame/dispatch [:set-sort key])} 
   (clojure.string/capitalize (name key))])

(defn herostats-table []
  (let [heroes (re-frame/subscribe [:table-data])]
    (fn []
      (let [statkeys (keys (first @heroes))]
        [:table 
         [:tr
          (for [statkey statkeys]
            (herostats-header-cell statkey))]
          (for [hero @heroes]
            (herostats-tablerow hero statkeys))]))))

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

(defn lan-list [lans]
  (fn []
    [:ul
     (for [lan lans]
       ^{:key lan} [:li (test-statsbox lan)])
     ]
    ))

(defn main-panel []
  [:div
   [lan-list lans]
   [herostats-table]
   ])
