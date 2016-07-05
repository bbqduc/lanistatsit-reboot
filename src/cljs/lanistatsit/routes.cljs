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

(defn init-routes []
  (secretary/set-config! :prefix "#")
  (defroute "/" []
    (re-frame/dispatch [:set-current-view :home]))
  (defroute "/heroes" []
    (re-frame/dispatch [:set-current-view :heroes]))
  (defroute "/players" []
    (re-frame/dispatch [:set-current-view :players]))
  (defroute "/lans" []
    (re-frame/dispatch [:set-current-view :lans]))
  (hook-browser-navigation!))
