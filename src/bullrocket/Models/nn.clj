(ns bullrocket.Models.nn
  (:use [enclog nnets training])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [enclog.nnets :as nnets]
            [enclog.training :as training]
            [clojure.set])
  (:import [org.encog.neural.networks PersistBasicNetwork]))

(defn convert-data [data row]
  (let [[date open _ _ close _] row]
    (if (and (number? (read-string open)) (number? (read-string close)))
      (assoc data date (if (< (read-string open) (read-string close)) 1 -1))
      data)))

(defn read-stocks [file]
  (with-open [f (io/reader file)]
    (doall (reduce #(convert-data %1 %2) {} (drop 1 (csv/read-csv f))))))

(defn make-net [n-features hidden-nodes]
  (nnets/network (nnets/neural-pattern :feed-forward)
                 :activation :sigmoid
                 :input n-features
                 :hidden [hidden-nodes]
                 :output 1))

(defn common-keys [ks]
  (let [ks (map set ks)]
    (apply clojure.set/intersection ks)))

(defn transform [common-dates data]
  (mapv #(reduce (fn [agg datum] (conj agg (get datum %))) [] data) common-dates))

(defn normalize [features target]
  (let [x (mapv #(read-stocks %) features)
        y (vector (read-stocks target))
        k (->> (mapv keys (conj x y))
               (drop-last) 
               (common-keys))
        x (transform k x)
        y (transform k y)]
    (vector x y)))

(defn train-nn [nn data alg iter exp-err]
  (let [trainer (trainer alg :network nn :traing-set data)]
    (train trainer exp-err iter [])))
