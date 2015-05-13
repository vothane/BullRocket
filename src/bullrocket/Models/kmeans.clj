(ns bullrocket.Models.kmeans)

(defn distance [a b]
  (if (< a b) (- b a) (- a b)))

(defn closest [point means distance]
  (first (sort-by #(distance % point) means)))

(defn point-groups [means data distance]
  (group-by #(closest % means distance) data))

(defn average [& list]
  (/ (reduce + list)
     (count list)))

(defn new-means [average point-groups old-means]
  (for [m old-means]
    (if (contains? point-groups m)
      (apply average (get point-groups m))
      m)))

(defn iterate-means [data distance average]
  (fn [means]
    (new-means average
               (point-groups means data distance) means)))

(defn k-cluster [data distance means]
  (vals (point-groups means data distance)))

(defn take-while-unstable
  ([sq] (lazy-seq (if-let [sq (seq sq)]
                    (cons (first sq)
                          (take-while-unstable
                           (rest sq) (first sq))))))
  ([sq last] (lazy-seq (if-let [sq (seq sq)]
                         (if (= (first sq) last)
                           nil
                           (take-while-unstable sq))))))

(defn k-groups [data distance average]
  (fn [guesses]
    (take-while-unstable
     (map #(k-cluster data distance %)
          (iterate (iterate-means data distance average)
                   guesses)))))

(defn vec-distance [a b]
  (reduce + (map #(* % %) (map - a b))))

(defn vec-average [& list]
  (map #(/ % (count list)) (apply map + list)))
