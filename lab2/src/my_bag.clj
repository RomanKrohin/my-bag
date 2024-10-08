(ns my-bag)

;; Протокол для мешка (Bag)
(defprotocol BagProtocol
  (add-to-bag [bag key element] "Добавляет элемент в мешок по ключу")
  (remove-one-from-bag [bag key element] "Удаляет элемент из мешка по ключу")
  (merge-bags [bag1 bag2] "Объединяет два мешка")
  (filter-bag [bag pred] "Фильтрует элементы в мешке по предикату")
  (map-bag [bag f] "Отображает элементы мешка с помощью функции")
  (fold-left [bag f init] "Левосторонняя свёртка для мешка")
  (fold-right [bag f init] "Правосторонняя свёртка для мешка"))

;; Реализация мешка через defrecord
(defrecord Bag [elements]
  BagProtocol
  ;; Добавление элемента
  #_{:clj-kondo/ignore [:unused-binding]}
  (add-to-bag [this key element]
    (let [updated-elements (update elements (hash key) #(conj (or % []) element))]
      (Bag. updated-elements)))

  ;; Удаление элемента
  (remove-one-from-bag [this key element]
    (let [hash-key (hash key)
          current-list (get elements hash-key [])]
      (if (empty? current-list)
        this
        (let [new-list (rest (drop-while #(not= % element) current-list))]
          (if (empty? new-list)
            (Bag. (dissoc elements hash-key))
            (Bag. (assoc elements hash-key new-list)))))))

  ;; Объединение двух мешков
  #_{:clj-kondo/ignore [:unused-binding]}
  (merge-bags [this other-bag]
    (let [merged-elements (merge-with into elements (:elements other-bag))]
      (Bag. merged-elements)))

  ;; Фильтрация элементов по предикату
  #_{:clj-kondo/ignore [:unused-binding]}
  (filter-bag [this pred]
    (let [filtered-elements (into {} (map (fn [[k v]] [k (filter pred v)]) elements))]
      (Bag. filtered-elements)))

  ;; Левосторонняя свёртка
  #_{:clj-kondo/ignore [:unused-binding]}
  (fold-left [this f init]
    (reduce
     (fn [acc [k v]]
       (reduce #(f %1 %2) acc v))
     init
     elements))

  ;; Правосторонняя свёртка
  #_{:clj-kondo/ignore [:unused-binding]}
  (fold-right [this f init]
    (reduce
     (fn [acc [k v]]
       (reduce #(f %2 %1) acc (reverse v)))
     init
     elements)))

;; Функция для создания пустого мешка
(defn create-bag []
  (->Bag {}))

;; Пример использования:
(def bag (create-bag))
(def updated-bag (-> bag
                     (add-to-bag :key1 1)
                     (add-to-bag :key1 2)
                     (add-to-bag :key2 3)))

(println "Updated Bag:" updated-bag)

;; Проверим объединение
(def another-bag (->Bag {:key1 [3 4] :key3 [5]}))
(def merged-bag (merge-bags updated-bag another-bag))

(println "Merged Bag:" merged-bag)

;; Фильтрация
(def filtered-bag (filter-bag merged-bag even?))
(println "Filtered Bag:" filtered-bag)