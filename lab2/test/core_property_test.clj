(ns core-property-test
  (:require [clojure.test :refer :all]
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

;; Генератор для ключа (он будет хэшироваться)
(def generate-key
  gen/any)

;; -----------------------------------------------------------------------
;;                         Property-based тесты

;; Свойство моноида: объединение пустого мешка с любым мешком возвращает исходный мешок
(defspec merge-empty-bag
  100
  (prop/for-all [bag generate-bag]
                (let [empty (create-bag)]
                  (and (= (merge-bags empty bag) bag)
                       (= (merge-bags bag empty) bag)))))

;; Свойство инвариантности: после добавления и удаления элемента по ключу мешок остается таким же
(defspec add-remove-invariant
  100
  (prop/for-all [key generate-key
                 element generate-element
                 bag generate-bag]
                (let [new-bag (add-to-bag bag key element)]
                  (= (remove-one-from-bag new-bag key element) bag))))

;; Свойство идемпотентности: добавление элемента дважды сохраняет порядок для ключа
(defspec idempotent-add
  100
  (prop/for-all [key generate-key
                 element generate-element
                 bag generate-bag]
                (let [new-bag (add-to-bag (add-to-bag bag key element) key element)]
                  (= (get new-bag (hash key)) (concat (get bag (hash key)) [element element])))))

;; Свойство фильтрации: фильтрация элементов должна оставлять только те элементы, которые удовлетворяют предикату
(defspec filter-bag-test
  100
  (prop/for-all [bag generate-bag]
                (let [even? (fn [x] (zero? (mod x 2)))
                      filtered (filter-bag bag even?)]
                  (every? (fn [[_ v]] (every? even? v)) filtered))))
