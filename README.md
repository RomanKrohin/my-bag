

---

# Отчет по лабораторной работе: Реализация неизменяемого мешка (Separate Chaining Hashmap)

## Цель

Цель данной лабораторной работы — освоиться с построением пользовательских типов данных, полиморфизмом, рекурсивными алгоритмами и средствами тестирования (unit testing, property-based testing). В рамках работы была реализована структура данных — неизменяемый мешок (hash bag), использующий метод отдельной цепочки (separate chaining).

## Требования к разработанному ПО

1. Реализовать функции:
   - добавление и удаление элементов;
   - фильтрация;
   - отображение (map);
   - свертки (левая и правая);
   - структура должна быть моноидом.

2. Структуры данных должны быть неизменяемыми.
3. Библиотека должна быть протестирована в рамках unit testing.
4. Библиотека должна быть протестирована в рамках property-based testing (как минимум 3 свойства, включая свойства моноида).
5. Структура должна быть полиморфной.
6. Использовать идиоматичный для технологии стиль программирования.

## Ключевые элементы реализации

### Определение структуры данных

Создание пустого мешка с использованием отдельной цепочки:

```clojure
(defn create-bag []
  {})
```

### Добавление элемента

Функция `add-to-bag` добавляет элемент в мешок. Для этого используется функция `update`, которая создает новый мешок с добавленным элементом:

```clojure
(defn add-to-bag [bag element]
  (update bag (hash element) #(conj (or % []) element)))
```

### Удаление элемента

Функция `remove-one-from-bag` удаляет один элемент из мешка. Она возвращает новый мешок без изменяемых данных:

```clojure
(defn remove-one-from-bag [bag element]
  (let [hash-key (hash element)
        current-list (get bag hash-key [])]
    (if (empty? current-list)
      bag
      (let [new-list (rest (drop-while #(not= % element) current-list))]
        (if (empty? new-list)
          (dissoc bag hash-key)
          (assoc bag hash-key new-list))))))
```

### Фильтрация

Функция `filter-bag` фильтрует элементы в мешке по заданному предикату и возвращает новый мешок:

```clojure
(defn filter-bag [bag pred]
  (into {} (map (fn [[k v]] [k (filter pred v)]) bag)))
```

### Отображение (map)

Функция `map-bag` применяет функцию к элементам мешка и возвращает новый мешок:

```clojure
(defn map-bag [bag f]
  (reduce
   (fn [new-bag [k v]]
     (merge-with into new-bag {(hash (f k)) (map f v)}))
   empty-bag
   bag))
```

### Свертки

Слевая и правая свертки реализованы в функциях `fold-left` и `fold-right` соответственно:

```clojure
(defn fold-left [bag f init]
  (reduce
   (fn [acc [k v]]
     (reduce #(f %1 %2) acc v))
   init
   bag))

(defn fold-right [bag f init]
  (reduce
   (fn [acc [k v]]
     (reduce #(f %2 %1) acc (reverse v)))
   init
   bag))
```

### Неизменяемость

Коллекция неизменяемая, поскольку все функции возвращают новые структуры данных без изменения исходного мешка. Например, `add-to-bag` возвращает новый мешок с добавленным элементом, а `remove-one-from-bag` возвращает новый мешок с удаленным элементом:

```clojure
(def empty-bag
  (create-bag))
```

Каждая операция возвращает новую версию мешка, а исходный мешок остается неизменным.

## Тестирование

### Unit Testing

Для тестирования были реализованы следующие тесты:

```clojure
(deftest test-add-to-bag
  (testing "Add element to bag"
    (let [bag (add-to-bag empty-bag 1)]
      (is (= (get bag (hash 1)) [1])))))

(deftest test-remove-one-from-bag
  (testing "Remove one element from bag"
    (let [bag (-> (add-to-bag empty-bag 1)
                  (add-to-bag 1)
                  (remove-one-from-bag 1))]
      (is (= (get bag (hash 1)) [1])))))
```

### Property-Based Testing

Были протестированы свойства мешка:

1. Если в мешке нет элементов, то удаление не меняет мешок.
2. Добавление элемента к мешку увеличивает его размер.
3. Свертка элементов дает ожидаемый результат.

```clojure
(defspec filter-always-returns-smaller-or-equal-size
  100
  (prop/for-all [bag (gen/bag)]
    (let [filtered (filter-bag bag #(> % 0)]
          original-size (count bag)
          filtered-size (count filtered)]
      (<= filtered-size original-size))))

(defspec add-to-bag-preserves-invariants
  100
  (prop/for-all [bag (gen/bag) element (gen/int)]
    (let [new-bag (add-to-bag bag element)]
      (contains? new-bag (hash element)))))
```

## Выводы

В ходе реализации лабораторной работы был изучен процесс создания неизменяемых структур данных на языке Clojure. Реализация мешка с использованием метода отдельной цепочки позволила углубить понимание работы хеш-таблиц и полиморфизма. Использование спецификаций и свойств для тестирования обеспечило надежность и стабильность библиотеки, позволяя уверенно добавлять новые функции.
