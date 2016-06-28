(ns lanistatsit.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
  :sort-key
  (fn [db _]
    (reaction (:sort-key @db))))

(re-frame/register-sub
  :sort-reverse
  (fn [db _]
    (reaction (:sort-reverse @db))))

(re-frame/register-sub
  :data
  (fn [db _]
    (reaction (:data @db))))

(re-frame/register-sub
  :table-data
  (fn [db _]
    (let [sort-key (re-frame/subscribe [:sort-key])
          sort-reverse (re-frame/subscribe [:sort-reverse])
          data (re-frame/subscribe [:data])]
      (reaction 
        (let [newdata (sort-by @sort-key @data)]
          (if @sort-reverse (reverse newdata) newdata))
        )
      )))
