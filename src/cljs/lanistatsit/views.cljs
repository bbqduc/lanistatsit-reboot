(ns lanistatsit.views
  (:require [re-frame.core :as re-frame]
            [goog.string :as gstring]
            [goog.string.format]
            ))

(defonce lans [{:lan "Lan 1", :wins 10, :losses 8}
               {:lan "Lan 2", :wins 15, :losses 11}])

(defn sortable-table-row [data data-keys id]
  ^{:key id} [:tr
   (for [data-key data-keys]
     (let [trans (:transform data-key)
           value (get data (:key data-key))
           content (if (nil? trans) value (trans value))]
       ^{:key (name (:key data-key))} [:td content]))])

(defn sort-icon
  "Sorting icon for lists"
  [reversed?]
  (if reversed? "\u25bc" "\u25b2"))

(defn sortable-table-header-cell [table-id data-key sort-key reversed?]
  ^{:key (name data-key)}
  [:th
   {:on-click #(re-frame/dispatch [:set-sort data-key table-id])}
   (name data-key)
   (let [icon (if (= (name sort-key) (name data-key)) (sort-icon reversed?) "")]
     [:span {:class "sort-icon"} icon])])

(defn sortable-table [data-id table-id data-keys table-modifiers]
  (fn []
    [:table.w3-table.w3-striped.w3-white table-modifiers
       [:thead
        (let [data-sub (re-frame/subscribe [:table-data data-id table-id])
              sort-key (:sort-key (meta @data-sub))
              reversed? (:reverse (meta @data-sub))]
          (for [data-key (map #(get-in % [:key]) data-keys)]
            (sortable-table-header-cell table-id data-key sort-key reversed?)))]
       [:tbody
        (let [data-sub (re-frame/subscribe [:table-data data-id table-id])]
          (map-indexed #(sortable-table-row %2 data-keys %1) @data-sub)
          )]]))

(defn percentage-string [percentage]
  (str (* 100 percentage) "%"))

(defn test-statsbox [stats]
  (let [lan (:lan stats)
        wins (:wins stats)
        losses (:losses stats)]
    [:div
     [:div.w3-right
      [:h4 (gstring/format "%.2f%" (percentage-string (/ wins (+ wins losses))))]
      [:h4 (str wins "/" losses)]]
     [:div.w3-clear]
     [:a {:href (str "/lans/" lan)} [:h4 lan]]]))

(defn lan-list [lans]
  (fn []
    [:div.w3-row-padding.w3-margin-bottom
     [:h4 "Winrates"]
     (for [lan lans]
       [:div.w3-quarter {:key (str "q_" lan)}
        [:div.w3-container.w3-red.w3-padding-16
         [:div.w3-left
          [:i.fa.fa-bar-chart.w3-xxxlarge]]
          ^{:key lan} (test-statsbox lan)]])]))

(defn index []
  [:div
   [lan-list lans]
   [:div.w3-container.w3-row-padding
    [:h4 "Hero stats for all LANs"]
    [sortable-table :data :herostats-table
     [{:key :name, :transform (fn [x] [:a {:href (str "/hero/" x)} x])}
      {:key :wins}
      {:key :losses}]]]
   [:div.w3-container.w3-row-padding {:id "playerstatslabel"}
    [:h4 "Player stats for all LANs"]
    [sortable-table :players :players-table
     [{:key :name, :transform (fn [x] [:a {:href (str "/player/" x)} x])}]
     {:class "herostats"}]]])
   ;[:a {:href "/#halloo"} "hallo world"]])

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
