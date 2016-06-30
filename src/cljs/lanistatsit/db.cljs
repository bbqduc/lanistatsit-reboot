(ns lanistatsit.db)

(def default-db
  {:name "re-frame",
   :sort-key :name,
   :sort-reverse false,
   :data [{:name "Hero1", :wins 10, :losses 15}
          {:name "Hero2", :wins 20, :losses 12}]
   :view :home
   })
