(ns lanistatsit.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))
            ;[cljs.core.match :refer-macros [match]]))

(re-frame/register-sub
 :current-view
 (fn [db _]
   (reaction (:view @db))))

(defn winrate [wins losses]
  (if (and (zero? wins) (zero? losses))
    0
    (* 100 (/ wins (+ wins losses)))))

(re-frame/register-sub
 :hero-stats
 (fn [db _]
   (reaction
    (map #(assoc % :winrate (winrate (:wins %) (:losses %)))
         (:data @db)))))

(re-frame/register-sub
 :player-stats
 (fn [db _]
   (reaction
    (:players @db))))

(re-frame/register-sub
 :filtered-data
 (fn [db [_ data-key filter-keys]]
   (let [data-sub (re-frame/subscribe [data-key])]
     (reaction
      (let [filterstring (get-in @db filter-keys)
            data @data-sub]
        (if (empty? filterstring)
          data
          (filter #(re-find (re-pattern (str "(?i)" filterstring)) (:name %))
                  data)
          ))))))

(re-frame/register-sub
 :table-data
 (fn [db [_ data-key table-info-key]]
   ;(let [data-sub (re-frame/subscribe [data-key])]
   (let [data-sub (re-frame/subscribe [:filtered-data data-key [table-info-key :filter]])]
     (reaction
      (let [table-info (get @db table-info-key)
            newdata (sort-by (:sort-key table-info) @data-sub)
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
