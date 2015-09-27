(ns bullrocket.Models.nn-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.nn :refer :all])
  (:use [enclog nnets training]))

(deftest parse-csv-test
  (testing "should parse csv"
    (let [f ["test/bullrocket/data/FXA.csv" "test/bullrocket/data/FXB.csv" "test/bullrocket/data/FXC.csv"]
          t "test/bullrocket/data/GLD.csv" 
          d (normalize f t)]
      (is (= [] [])))))

(deftest train-test
  (testing "should train neural network"
    (let [f ["test/bullrocket/data/FXA.csv" "test/bullrocket/data/FXB.csv" "test/bullrocket/data/FXC.csv"]
          t "test/bullrocket/data/GLD.csv" 
          [x y] (normalize f t)
          d (data :basic-dataset x y)
          nn (make-net 3 3)
          nn (train-nn nn d :back-prop 10 0.25) _ (println nn)]
      (is (= nn [])))))
