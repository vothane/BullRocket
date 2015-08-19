(ns bullrocket.Models.nn-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.nn :refer :all])
  (:use     [enclog nnets training]))

(def mlp (network (neural-pattern :feed-forward) 
                   :activation :sigmoid
                   :input 2
                   :output 1
                   :hidden [3]))

(defn train-nn [nn data alg]
  (let [trainer (trainer alg :network nn :training-set data)]
    (train trainer 0.01 250 [])))

(def dataset
  (let [x [[0.0 0.0] [1.0 0.0] [0.0 1.0] [1.0 1.0]]
        y [[0.0] [1.0] [1.0] [0.0]]]
    (data :basic-dataset x y)))


(def MLP (train-nn mlp dataset :back-prop))

(defn run-nn [nn input]
  (let [in  (data :basic input)
        out (.compute nn in)]
    (.getData out)))

(deftest nn-test
  (testing "experiment"
    (is (= (run-nn MLP [0 0]) [0]))))
