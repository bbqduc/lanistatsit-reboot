(ns lanistatsit.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [lanistatsit.views :as views]
            )
  (:import goog.History))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn init-routes []
  (secretary/set-config! :prefix "#")
  (defroute "/" []
    (swap! lanistatsit.core/app-state assoc :page :home))
  (defroute "/halloo" []
    (swap! lanistatsit.core/app-state assoc :page :halloo))
  (hook-browser-navigation!))

(defmulti current #(@lanistatsit.core/app-state :page))
(defmethod current :home []
  (views/main-panel))
(defmethod current :halloo []
  (views/halloo))
(defmethod current :default []
  (views/main-panel))
