(ns core-property-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [core :refer :all]))

;; -----------------------------------------------------------------------
;;                         Generators

;; Генератор для мешков с произвольными элементами
(def generate-bag
  (gen/map gen/int (gen/vector gen/int)))

;; Генератор для произвольного элемента
(def generate-element
  gen/int)

;; -----------------------------------------------------------------------
;;                         Property-based тесты

;; Свойство моноида: объединение пустого мешка с любым мешком возвращает исходный мешок
(defspec merge-empty-bag
  100
  (prop/for-all [bag generate-bag]
                (let [empty (create-bag)]
                  (and (= (merge-bags empty bag) bag)
                       (= (merge-bags bag empty) bag)))))

;; Свойство инвариантности: после добавления и удаления элемента мешок остается таким же
(defspec add-remove-invariant
  100
  (prop/for-all [element generate-element
                 bag generate-bag]
                (let [new-bag (add-to-bag bag element)]
                  (= (remove-one-from-bag new-bag element) bag))))

;; Свойство идемпотентности: добавление элемента дважды сохраняет порядок
(defspec idempotent-add
  100
  (prop/for-all [element generate-element
                 bag generate-bag]
                (let [new-bag (add-to-bag (add-to-bag bag element) element)]
                  (= (get new-bag (hash element)) (concat (get bag (hash element)) [element element])))))

;; Свойство фильтрации: фильтрование элементов должно оставлять только те элементы, которые удовлетворяют предикату
(defspec filter-bag-test
  100
  (prop/for-all [bag generate-bag]
                (let [even? (fn [x] (zero? (mod x 2)))
                      filtered (filter-bag bag even?)]
                  (every? (fn [[_ v]] (every? even? v)) filtered))))
