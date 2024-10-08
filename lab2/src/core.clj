(ns core
  (:require [clojure.spec.alpha :as s]))

;; Создаёт пустой мультисет (Bag)
(defn create-bag []
  {})

;; Функция для добавления элемента в мультисет по ключу
(defn add-to-bag [bag key element]
  (update bag (hash key) #(conj (or % []) element)))

;; Функция для удаления одного экземпляра элемента из мультисета по ключу
(defn remove-one-from-bag [bag key element]
  (let [hash-key (hash key)
        current-list (get bag hash-key [])]
    (if (empty? current-list)
      bag
      (let [new-list (rest (drop-while #(not= % element) current-list))]
        (if (empty? new-list)
          (dissoc bag hash-key)
          (assoc bag hash-key new-list))))))

;; Функция для объединения двух мультисетов
(defn merge-bags [bag1 bag2]
  (merge-with into bag1 bag2))

;; Пустой мультисет
(def empty-bag
  (create-bag))

;; Фильтрация элементов в мультисете по предикату
(defn filter-bag [bag pred]
  (into {} (map (fn [[k v]] [k (filter pred v)]) bag)))

;; Отображение элементов мультисета с помощью функции
(defn map-bag [bag f]
  (reduce
   (fn [new-bag [k v]]
     (assoc new-bag k (map f v)))
   empty-bag
   bag))

;; Левосторонняя свёртка для мультисета
(defn fold-left [bag f init]
  (reduce
   #_{:clj-kondo/ignore [:unused-binding]}
   (fn [acc [k v]]
     (reduce #(f %1 %2) acc v))
   init
   bag))

;; Правосторонняя свёртка для мультисета
(defn fold-right [bag f init]
  (reduce
   #_{:clj-kondo/ignore [:unused-binding]}
   (fn [acc [k v]]
     (reduce #(f %2 %1) acc (reverse v)))
   init
   bag))

;; Спецификации для функций
(s/fdef add-to-bag
  :args (s/cat :bag map? :key any? :element any?)
  :ret map?)

(s/fdef remove-one-from-bag
  :args (s/cat :bag map? :key any? :element any?)
  :ret map?)
