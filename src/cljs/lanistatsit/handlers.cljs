(ns lanistatsit.handlers
    (:require [re-frame.core :as re-frame]
              [lanistatsit.db :as db]))

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
                       :sort-reverse newreverse})
      )))

(re-frame/register-handler
 :set-current-view
 (fn  [db [_ view]]
   (assoc db :view view)))
