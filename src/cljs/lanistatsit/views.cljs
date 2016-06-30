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
  [:th 
   {:on-click #(re-frame/dispatch [:set-sort key])} 
   (clojure.string/capitalize (name key))])

(defn herostats-table []
  (let [heroes (re-frame/subscribe [:table-data])]
    (fn []
      (let [statkeys (keys (first @heroes))]
        [:table {:id "herostats"}
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
    [:div {:id "lanlistentry"}
       lan
       [:ul {:id "lanlist"}
        [:li (gstring/format "Winrate: %.2f%" (percentage-string (/ wins (+ wins losses))))]
        [:li (str wins "/" losses)]
        ]]))

(defn lan-list [lans]
  (fn []
    [:div {:id "lanlist"}
     (for [lan lans]
       ^{:key lan} (test-statsbox lan))
     ]
    ))

(defn index []
  [:div
   [lan-list lans]
   [herostats-table]
   [:a {:href "/#halloo"} "hallo world"]])

(defn halloo []
  [:div
   [:h1 "Halloota"]
   [:a {:href "/#"} "Home"]])

(defmulti views identity)
(defmethod views :home [] [index])
(defmethod views :halloo [] [halloo])
(defmethod views :default [] [index])

(defn show-view [view]
  [views view])

(defn current-view []
  (let [current (re-frame/subscribe [:current-view])]
    (fn []
      [show-view @current])))
