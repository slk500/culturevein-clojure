(ns frontend.culturevein-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]))

(deftest addition-tests
  (is (= 5 (+ 3 2)))
  (is (= 2 (+ 3 2)))
  (is (= 10 (+ 5 5))))

