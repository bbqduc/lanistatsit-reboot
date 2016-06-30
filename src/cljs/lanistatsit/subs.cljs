(ns lanistatsit.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :current-view
  (fn [db _]
    (reaction (:view @db))))

(re-frame/register-sub
  :table-data
  (fn [db [_ data-key table-info-key]]
    (reaction
      (let [table-info (get @db table-info-key)
            data (get @db data-key)
            newdata (sort-by (:sort-key table-info) data)]
        (if (:sort-reverse table-info) (reverse newdata) newdata))
      ))
  )
