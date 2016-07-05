(ns lanistatsit.views
  (:require [re-frame.core :as re-frame]
            [goog.string :as gstring]
            [goog.string.format]
            ))

(defonce lans-data [{:lan "Lan 1", :wins 10, :losses 8}
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
     [:span.sort-icon icon])])

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
    [:div.w3-container.w3-row-padding.w3-margin-bottom
     [:h4 "Winrates"]
     (for [lan lans]
       [:div.w3-quarter {:key (str "q_" lan)}
        [:div.w3-container.w3-red.w3-padding-16
         [:div.w3-left
          [:i.fa.fa-bar-chart.w3-xxxlarge]]
          ^{:key lan} (test-statsbox lan)]])]))

(defn top-bar
  "Black bar on top of the app"
  []
  [:div.w3-container.w3-top.w3-black.w3-large.w3-padding {:style {:zIndex 4}}
   [:button.w3-btn.w3-hide-large.w3-padding-0.w3-hover-text-grey {:on-click #(re-frame/dispatch [:open-menu])}
    [:i.fa.fa-bars]
    " Menu"]
   [:span.w3-right "Lanistatsit"]])

(defn side-navigation
  "Side navigation bar"
  []
  (let [menu-display-css (re-frame/subscribe [:menu-display-css])]
    [:nav.w3-sidenav.w3-collapse.w3-white.w3-animate-left {:style {:zIndex 3 :width "300px" :display @menu-display-css}}
     [:a.w3-padding-16.w3-hide-large.w3-dark-grey.w3-hover-black {:href "#" :title "close menu" :on-click #(re-frame/dispatch [:close-menu])}
      [:i.fa.fa-remove.fa-fw] "Close Menu"]
     [:a.w3-padding.w3-blue {:href "#"}
      [:i.fa.fa-users.fa-fw]
      "Overview"]
     [:a.w3-padding {:href "/#heroes"}
      [:i.fa.fa-eye.fa-fw]
      "Hero stats"]
     [:a.w3-padding {:href "/#players"}
      [:i.fa.fa-users.fa-fw]
      "Player stats"]
     [:a.w3-padding {:href "/#lans"}
      [:i.fa.fa-bullseye.fa-fw]
      "Lan parties"]]))

(defn side-navigation-overlay
  "Dark overlay that is visible on small screens when the side navigation is open.
  In a different component because it has to be outside the <nav> of side-navigation"
  []
  (let [menu-display-css (re-frame/subscribe [:menu-display-css])]
    [:div.w3-overlay.w3-hide-large.w3-animate-opacity {:style {:cursor "pointer" :display @menu-display-css} :title "close side menu"}]))

(defn main-container
  "Container where page content sits in"
  [content]
  [:div
   (top-bar)
   (side-navigation)
   (side-navigation-overlay)
   [:div.w3-main {:style {:marginLeft "300px" :marginTop "43px"}}
    (content)]])

(defn header
  "Header element for a view"
  [text]
  [:header.w3-container {:style {:paddingTop "22px"}}
   [:h3 [:b
         [:i.fa.fa-dashboard]
         (str " " text)]]])


(defn heroes []
  [:div.w3-container.w3-row-padding.w3-margin-bottom
   [:h4 "Hero stats for all LANs"]
   ^{:key "heroes-table"}[sortable-table :data :herostats-table
    [{:key :name, :transform (fn [x] [:a {:href (str "/hero/" x)} x])}
     {:key :wins}
     {:key :losses}]]])

(defn players []
  [:div.w3-container.w3-row-padding.w3-margin-bottom {:id "playerstatslabel"}
   [:h4 "Player stats for all LANs"]
   ^{:key "players-table"}[sortable-table :players :players-table
    [{:key :name, :transform (fn [x] [:a {:href (str "/player/" x)} x])}]]])

(defn lans []
  [lan-list lans-data])

(defn index []
  [:div
   (header "Overview")
   (lans)
   (heroes)
   (players)
   ])

(defmulti views identity)
(defmethod views :home [] (main-container index))
(defmethod views :heroes [] (main-container heroes))
(defmethod views :players [] (main-container players))
(defmethod views :lans [] (main-container lans))
(defmethod views :default [] [(main-container index)])

(defn current-view []
  (let [current (re-frame/subscribe [:current-view])]
    (fn []
      [views @current])))
