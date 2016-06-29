(ns lanistatsit.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [[:table {:color "red"}]
   [:ul {:padding 0}
    [:li {:display "inline",
     :background-color "black",
          :color "white",
          :padding "10px 20px",
          :text-decoration "none",
          :border-radius "4px 4px 0 0"}
      [:&:hover
       {:background-color "orange"}
       ]
      ]]])
