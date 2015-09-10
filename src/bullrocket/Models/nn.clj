(ns bullrocket.Models.nn
  (:use [enclog nnets training])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [enclog.nnets :as nnets]
            [enclog.training :as training])
  (:import [org.encog.neural.networks PersistBasicNetwork]))

(defn convert-data [row]
  (let [[_ open _ _ close _] row]
    (if (< open close) 1 -1)))

(defn read-stocks [file]
  (with-open [f (io/reader file)]
    (doall (mapv convert-data (drop 1 (csv/read-csv f))))))

(defn make-net [n-features hidden-nodes]
  (nnets/network (nnets/neural-pattern :feed-forward)
                 :activation :sigmoid
                 :input n-features
                 :hidden [hidden-nodes]
                 :output 1))

(defn normalize [features target]
  (let [x (apply interleave (mapv #(read-stocks %) features))
        y (read-stocks target)]
    (vector x y)))
