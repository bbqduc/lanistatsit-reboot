(ns lanistatsit.views
  (:require [re-frame.core :as re-frame]
            [goog.string :as gstring]
            [goog.string.format]))

(defonce lans [{:lan "Lan 1", :wins 10, :losses 8}
               {:lan "Lan 2", :wins 15, :losses 11}])

(defn sortable-table-row [data data-keys]
  [:tr
   (for [data-key data-keys]
     (let [trans (:transform data-key)
           value (get data (:key data-key))]
       [:td (if (nil? trans) value (trans value))]))])

(defn sortable-table-header-cell [table-id data-key]
  [:th
   {:on-click #(re-frame/dispatch [:set-sort data-key table-id])}
   (clojure.string/capitalize (name data-key))])

(defn sortable-table [data-id table-id data-keys table-modifiers]
  (fn []
    (let [data-sub (re-frame/subscribe [:table-data data-id table-id])]
      [:table table-modifiers
       [:tr
        (for [data-key (map #(get-in % [:key]) data-keys)]
          (sortable-table-header-cell table-id data-key))]
       (for [data @data-sub]
         (sortable-table-row data data-keys))])))

(defn percentage-string [percentage]
  (str (* 100 percentage) "%"))

(defn test-statsbox [stats]
  (let [lan (:lan stats)
        wins (:wins stats)
        losses (:losses stats)]
    [:div {:id "lanlistentry"}
     [:a {:href (str "/lans/" lan)} lan]
     [:ul {:id "lanlist"}
      [:li (gstring/format "Winrate: %.2f%" (percentage-string (/ wins (+ wins losses))))]
      [:li (str wins "/" losses)]]]))

(defn lan-list [lans]
  (fn []
    [:div {:id "lanlist"}
     (for [lan lans]
       ^{:key lan} (test-statsbox lan))]))

(defn index []
  [:div
   [lan-list lans]
   [:div {:id "herostatslabel"}
    "Hero stats for all LANs"]
   [sortable-table :data :herostats-table
    [{:key :name, :transform (fn [x] [:a {:href (str "/hero/" x)} x])}
     {:key :wins}
     {:key :losses}]
    {:id "herostats"}]
   [:div {:id "playerstatslabel"}
    "Player stats for all LANs"
    [sortable-table :players :players-table
     [{:key :name, :transform (fn [x] [:a {:href (str "/player/" x)} x])}]
     {:id "herostats"}]]
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
