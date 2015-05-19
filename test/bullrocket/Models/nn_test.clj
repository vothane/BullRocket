(ns bullrocket.Models.nn-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.nn :refer :all]))

(def data [[[0 0] [0]]
           [[0 1] [1]]
           [[1 0] [1]]
           [[1 1] [0]]])

(def default-options
  {:max-iters 100
   :desired-error 0.20
   :hidden-neurons [3]
   :learning-rate 0.3
   :learning-momentum 0.01
   :weight-epsilon 50})

;(defn train [samples]
;  (let [network (MultiLayerPerceptron. default-options)]
;    (train-ann network samples)))

;(def NN (train data))

;(deftest run-binary-test
;  (testing "finding closet data point"
;    (is (=  (run-binary NN  [0 1]) [1]))
;    (is (=  (run-binary NN  [1 0]) [1]))
;    (is (=  (run-binary NN  [0 0]) [0]))
;    (is (=  (run-binary NN  [1 1]) [1]))))
