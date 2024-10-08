
#_{:clj-kondo/ignore [:require-use]}
(ns core-test
  (:require [clojure.test :refer [deftest is]]
            [core :refer [create-bag merge-bags add-to-bag remove-one-from-bag filter-bag fold-right fold-left]]))

(deftest test-add-to-bag 
  (let [bag (create-bag)]
    (is (= (add-to-bag bag :key1 1) {(hash :key1) [1]}))
    (is (= (add-to-bag (add-to-bag bag :key1 1) :key1 1) {(hash :key1) [1 1]}))))

(deftest test-remove-one-from-bag
  (let [bag (add-to-bag (add-to-bag (create-bag) :key1 1) :key1 1)]
    (is (= (remove-one-from-bag bag :key1 1) {(hash :key1) [1]}))
    (is (= (remove-one-from-bag (remove-one-from-bag bag :key1 1) :key1 1) {}))))

(deftest test-merge-bags
  (let [bag1 (add-to-bag (create-bag) :key1 1)
        bag2 (add-to-bag (create-bag) :key2 2)]
    (is (= (merge-bags bag1 bag2) {(hash :key1) [1] (hash :key2) [2]}))))

(deftest test-filter-bag
  (let [bag (add-to-bag (add-to-bag (create-bag) :key1 1) :key2 2)]
    (is (= (filter-bag bag even?) {(hash :key1) [] (hash :key2) [2]}))))

(deftest test-fold-left
  (let [bag (add-to-bag (add-to-bag (create-bag) :key1 1) :key2 2)]
    (is (= (fold-left bag + 0) 3))))

(deftest test-fold-right
  (let [bag (add-to-bag (add-to-bag (create-bag) :key1 1) :key2 2)]
    (is (= (fold-right bag + 0) 3))))
