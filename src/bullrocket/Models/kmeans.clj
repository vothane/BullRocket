(ns bullrocket.Models.kmeans
  (:use [clj-ml data clusterers])))

(def k-means-clusterer [num-k]
  (make-clusterer :k-means {:number-clusters num-k}))

(defn train-clusterer [clusterer dataset]
  (clusterer-build clusterer dataset)
  clusterer)

(def em-clusterer [num-k]
  (make-clusterer :expectation-maximization {:number-clusters num-k}))