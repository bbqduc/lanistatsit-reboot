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
 :menu-display-css
 (fn [db _]
   (reaction (:menu-display-css @db))))

(re-frame/register-sub
 :menu-active-view
 (fn [db _]
   (reaction (:menu-active-view @db))))
