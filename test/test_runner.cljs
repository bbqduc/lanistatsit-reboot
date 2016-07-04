(ns test.test-runner
  (:require [cljs.test :as tt]
            [doo.runner :refer-macros [doo-tests]]
            [test.handlers]))

(doo-tests 'test.handlers)
