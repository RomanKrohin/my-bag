(ns core
  (:require [clojure.spec.alpha :as s]))

;; Создает пустой мешок с использованием separate chaining
(defn create-bag []
  {})

;; Функция для добавления элемента в мешок
(defn add-to-bag [bag element]
  (update bag (hash element) #(conj (or % []) element)))

;; Функция для удаления одного элемента из мешка
(defn remove-one-from-bag [bag element]
  (let [hash-key (hash element)
        current-list (get bag hash-key [])]
    (if (empty? current-list)
      bag
      (let [new-list (rest (drop-while #(not= % element) current-list))]
        (if (empty? new-list)
          (dissoc bag hash-key)
          (assoc bag hash-key new-list))))))

;; Функция для объединения двух мешков
(defn merge-bags [bag1 bag2]
  (merge-with into bag1 bag2))

;; Пустой мешок
(def empty-bag
  (create-bag))

;; Фильтрация элементов в мешке по предикату
(defn filter-bag [bag pred]
  (into {} (map (fn [[k v]] [k (filter pred v)]) bag)))

;; Отображение элементов мешка с помощью функции
(defn map-bag [bag f]
  (reduce
   (fn [new-bag [k v]]
     (merge-with into new-bag {(hash (f k)) (map f v)}))
   empty-bag
   bag))

;; Слевая свёртка (fold-left)
(defn fold-left [bag f init]
  (reduce
   (fn [acc [k v]]
     (reduce #(f %1 %2) acc v))
   init
   bag))

;; Справа свёртка (fold-right)
(defn fold-right [bag f init]
  (reduce
   (fn [acc [k v]]
     (reduce #(f %2 %1) acc (reverse v)))
   init
   bag))

;; Спецификации для функций
(s/fdef add-to-bag
  :args (s/cat :bag map? :element any?)
  :ret map?)

(s/fdef remove-one-from-bag
  :args (s/cat :bag map? :element any?)
  :ret map?)
