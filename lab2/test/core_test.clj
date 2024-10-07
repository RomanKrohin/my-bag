(ns core-test
  (:require [clojure.test :refer :all]
            [core :refer :all]
            [core-property-test :refer :all]))

(deftest test-add-to-bag
  (let [bag (create-bag)]
    (is (= (add-to-bag bag 1) {(hash 1) [1]}))
    (is (= (add-to-bag (add-to-bag bag 1) 1) {(hash 1) [1 1]}))))

(deftest test-remove-one-from-bag
  (let [bag (add-to-bag (add-to-bag (create-bag) 1) 1)]
    (is (= (remove-one-from-bag bag 1) {(hash 1) [1]}))
    (is (= (remove-one-from-bag (remove-one-from-bag bag 1) 1) {}))))

(deftest test-merge-bags
  (let [bag1 (add-to-bag (create-bag) 1)
        bag2 (add-to-bag (create-bag) 2)]
    (is (= (merge-bags bag1 bag2) {(hash 1) [1] (hash 2) [2]}))))

(deftest test-filter-bag
  (let [bag (add-to-bag (add-to-bag (create-bag) 1) 2)]
    (is (= (filter-bag bag even?) {(hash 2) [2] (hash 1) []}))))

(deftest test-fold-left
  (let [bag (add-to-bag (add-to-bag (create-bag) 1) 2)]
    (is (= (fold-left bag + 0) 3))))

(deftest test-fold-right
  (let [bag (add-to-bag (add-to-bag (create-bag) 1) 2)]
    (is (= (fold-right bag + 0) 3))))
