(ns lanistatsit.key)

(def uniqkey (atom 0))
(defn gen-key []
  (swap! uniqkey inc))
