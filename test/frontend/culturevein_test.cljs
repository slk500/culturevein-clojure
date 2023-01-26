(ns frontend.culturevein-test
    (:require
     [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest addition-tests
  (is (= 5 (+ 3 2)))
  (is (= 10 (+ 5 5))))

(deftest flatten-children-relation
  (is (= [
          {"name" "parent", "children" []} 
          {"name" "child", "children" []}]
         [
          {"name" "parent", "children" [{"name" "parent", "children" []}]}])))

