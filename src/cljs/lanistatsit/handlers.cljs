(ns lanistatsit.handlers
    (:require [re-frame.core :as re-frame]
              [lanistatsit.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
  :set-sort
  (fn  [db [_ sortkey]]
    (let [flip (= (:sort-key db) sortkey)
          new-reverse (if flip (not (:sort-reverse db)) true)]
      (assoc db :sort-key sortkey 
             :sort-reverse new-reverse)
      )))

(re-frame/register-handler
 :set-current-view
 (fn  [db [_ view]]
   (assoc db :view view)))
