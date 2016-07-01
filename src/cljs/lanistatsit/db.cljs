(ns lanistatsit.db)

(def default-db
  {:herostats-table {:sort-key :name :sort-reverse false}
   :players-table {:sort-key :name :sort-reverse false}
   :data []
   :players [{:name "Player1"}
             {:name "Player2"}]
   :view :home})
