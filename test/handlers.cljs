(ns test.handlers
  (:require
   [cljs.test :refer-macros [deftest testing is]]
   [lanistatsit.handlers :as handlers]))

(defn- call-sort-and-validate [db newdb newkey]
  (= (handlers/set-sort-handler db newkey :test-table) newdb))

(deftest sort-handler
  (testing "sort-handler"
    (let [db          {:test-table {:sort-key :initial :sort-reverse false}}
          reversed-db {:test-table {:sort-key :initial :sort-reverse true}}
          newdb       {:test-table {:sort-key :new-sort :sort-reverse true}}]
      ; Assign a new sorting key
      (is (call-sort-and-validate db newdb :new-sort))
      ; Use the same sorting key to set :sort-reverse
      (is (call-sort-and-validate db reversed-db :initial))
      ; Use the same sorting key twice to get the original db back
      (let [reversed (handlers/set-sort-handler db :initial :test-table)]
        (is (call-sort-and-validate reversed-db db :initial)))
      ; Assign a different value to :sort-key
      (is (not (call-sort-and-validate db newdb :some-other-sort)))
      ; Use non-existing dbkey and expect to get non-modified db back
      (is (= (handlers/set-sort-handler db :initial :non-existing-table) db)))))
