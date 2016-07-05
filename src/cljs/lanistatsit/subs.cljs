(ns lanistatsit.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :current-view
 (fn [db _]
   (reaction (:view @db))))

(defn winrate [wins losses]
  (* 100 (/ wins (+ wins losses))))

(re-frame/register-sub
 :hero-stats
 (fn [db]
   (reaction
    (let [data (:data @db)]
      (map #(assoc % :winrate (winrate (:wins %) (:losses %)))
           data)))))

(re-frame/register-sub
 :player-stats
 (fn [db]
   (reaction
    (:players @db))))

(re-frame/register-sub
 :table-data
 (fn [db [_ data-key table-info-key]]
   (let [data-sub (re-frame/subscribe [data-key])]
     (reaction
      (let [table-info (get @db table-info-key)
            data @data-sub
            newdata (sort-by (:sort-key table-info) data)
            ret (if (:sort-reverse table-info) (reverse newdata) newdata)]
        (assoc table-info :data ret))))))

(re-frame/register-sub
 :menu-display-css
 (fn [db _]
   (reaction (:menu-display-css @db))))

(re-frame/register-sub
 :menu-active-view
 (fn [db _]
   (reaction (:menu-active-view @db))))
