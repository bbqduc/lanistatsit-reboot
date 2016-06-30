(ns lanistatsit.db)

(def default-db
  {:herostats-table {:sort-key :name :sort-reverse false}
   :players-table {:sort-key :name :sort-reverse false}
   :data [{:name "Hero1" :wins 10 :losses 15}
          {:name "Hero2" :wins 20 :losses 12}]
   :players [{:name "Player1"}
             {:name "Player2"}]
   :view :home
   })
