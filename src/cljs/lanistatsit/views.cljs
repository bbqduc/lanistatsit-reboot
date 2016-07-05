(ns lanistatsit.views
  (:require [re-frame.core :as re-frame]
            [goog.string :as gstring]
            [goog.string.format]))

(defonce lans-data [{:lan "Lan 1", :wins 10, :losses 8}
                    {:lan "Lan 2", :wins 15, :losses 11}])

(defmulti view-nav-icon identity)
(defmethod view-nav-icon :home [] "fa-dashboard")
(defmethod view-nav-icon :heroes [] "fa-eye")
(defmethod view-nav-icon :players [] "fa-users")
(defmethod view-nav-icon :lans [] "fa-bullseye")

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
  [:th.header
   [:div [:span {:on-click #(re-frame/dispatch [:set-sort data-key table-id])}
          (name data-key)
          (let [icon (if (= (name sort-key) (name data-key)) (sort-icon reversed?) "")]
            [:span.sort-icon icon])]
    (when (= data-key :name) [:input.headerinput {:type "text"
                                                  :on-change #(re-frame/dispatch [:set-table-filter table-id (-> % .-target .-value)])}])]])

(defn sortable-table [data-id table-id data-keys table-modifiers]
  (let [sub (re-frame/subscribe [:table-data data-id table-id])]
    (fn []
      (let [{sort-key :sort-key
             reversed? :sort-reverse
             data :data} @sub]
        [:table.w3-table.w3-striped.w3-white table-modifiers
         [:thead
          (for [data-key (map #(get-in % [:key]) data-keys)]
            (sortable-table-header-cell table-id data-key sort-key reversed?))]
         [:tbody
          (map-indexed #(sortable-table-row %2 data-keys %1) data)]]))))

(defn percentage-string [percentage]
  (str (* 100 percentage) "%"))

(defn winrate-string [wins losses]
  (gstring/format "Winrate: %.2f%" (percentage-string (/ wins (+ wins losses)))))

(defn test-statsbox [stats]
  (let [lan (:lan stats)
        wins (:wins stats)
        losses (:losses stats)]
    [:div
     [:div.w3-right
      [:h4 (winrate-string wins losses)]
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

(defn side-navigation-item
  "One entry for the side navigation bar"
  [def]
  (let [active (re-frame/subscribe [:current-view])]
    (fn []
      [:a.w3-padding {:href (str "/#" (.substr (:href def) 1)) :class (if (= (:view def) @active) "w3-blue" "")}
       [:i.fa {:class (view-nav-icon (:view def))}]
       (str " " (:text def))])))

(defn side-navigation
  "Side navigation bar"
  []
  (let [menu-display-css (re-frame/subscribe [:menu-display-css])]
    (fn []
      [:nav.w3-sidenav.w3-collapse.w3-white.w3-animate-left {:style {:zIndex 3 :width "300px" :display @menu-display-css}}
       [:a.w3-padding-16.w3-hide-large.w3-dark-grey.w3-hover-black {:title "close menu" :on-click #(re-frame/dispatch [:close-menu])}
        [:i.fa.fa-remove.fa-fw] "Close Menu"]
       (doall (for [route lanistatsit.routes/route-definitions]
                ^{:key (:href route)} [side-navigation-item route]))])))

(defn side-navigation-overlay
  "Dark overlay that is visible on small screens when the side navigation is open.
  In a different component because it has to be outside the <nav> of side-navigation"
  []
  (let [menu-display-css (re-frame/subscribe [:menu-display-css])]
    (fn []
      [:div.w3-overlay.w3-hide-large.w3-animate-opacity {:style {:cursor "pointer" :display @menu-display-css} :title "close side menu"}])))

(defn main-container
  "Container where page content sits in"
  [content]
  [:div
   (top-bar)
   [side-navigation]
   [side-navigation-overlay]
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
   ^{:key "heroes-table"} [sortable-table :hero-stats :herostats-table
                           [{:key :name, :transform (fn [x] [:a {:href (str "/hero/" x)} x])}
                            {:key :wins}
                            {:key :losses}
                            {:key :winrate :transform (fn [x] (gstring/format "%.1f%" x))}]]])

(defn players []
  [:div.w3-container.w3-row-padding.w3-margin-bottom {:id "playerstatslabel"}
   [:h4 "Player stats for all LANs"]
   ^{:key "players-table"} [sortable-table :player-stats :players-table
                            [{:key :name, :transform (fn [x] [:a {:href (str "/player/" x)} x])}]]])

(defn lans []
  [lan-list lans-data])

(defn index []
  [:div
   (header "Overview")
   (lans)
   (heroes)
   (players)])

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
