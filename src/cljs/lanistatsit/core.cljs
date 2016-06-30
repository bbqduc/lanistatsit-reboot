(ns lanistatsit.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [lanistatsit.handlers]
              [lanistatsit.subs]
              [lanistatsit.views :as views]
              [lanistatsit.routes :as routes]
              [lanistatsit.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(def app-state (reagent/atom {}))

(defn mount-root []
  (reagent/render [routes/current]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (routes/init-routes)
  (dev-setup)
  (mount-root))
