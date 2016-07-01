(ns lanistatsit.handlers
  (:require [re-frame.core :as re-frame]
            [lanistatsit.db :as db]
            [ajax.core :refer [GET]]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-sort
 (fn  [db [_ newkey dbkey]]
   (let [oldinfo (get db dbkey)
         oldkey (:sort-key oldinfo)
         oldreverse (:sort-reverse oldinfo)
         flip (= oldkey newkey)
         newreverse (if flip (not oldreverse) true)]
     (assoc db dbkey {:sort-key newkey
                      :sort-reverse newreverse}))))

(re-frame/register-handler
 :request-hero-stats
 (fn
   [db _]
   (GET
     "http://127.0.0.1:8080/stats"
     {:response-format :json
      :keywords?       true
      :handler         #(re-frame/dispatch [:process-stats %1])
      :error-handler   #(re-frame/dispatch [:process-stats-failed %1])})
   db))

(re-frame/register-handler
 :process-stats
 (fn
   [db [_ resp]]
   (let [data (get resp :_field0)]
     (assoc db :data data))))

(re-frame/register-handler
 :process-stats-failed
 (fn
   [db [_ resp]]
   (print (str "Failed"))
   db))

(re-frame/register-handler
 :set-current-view
 (fn  [db [_ view]]
   (assoc db :view view)))
