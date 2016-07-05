(ns lanistatsit.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [lanistatsit.views :as views]
            [re-frame.core :as re-frame])
  (:import goog.History))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defonce route-definitions
  [{:view :home :href "/" :text "Overview"}
   {:view :heroes :href "/heroes" :text "Hero stats"}
   {:view :players :href "/players" :text "Player stats"}
   {:view :lans :href "/lans" :text "Lan parties"}])

(defn init-routes []
  (secretary/set-config! :prefix "#")
  (dorun
   (map
    #(do
       (defroute (:href %1) []
         (re-frame/dispatch [:set-current-view (:view %1)]))) route-definitions))
  (hook-browser-navigation!))
