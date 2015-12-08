(ns bullrocket.Models.nn-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.nn :refer :all])
  (:use [enclog nnets training]))

(deftest baskets-with-some-correlation-test
  (testing "should indicate if basket items has some correlation"
    (let [f1 ["test/bullrocket/data/FXF.csv" "test/bullrocket/data/FXB.csv" "test/bullrocket/data/CYB.csv"]
          t1 "test/bullrocket/data/FXE.csv"
          f2 ["test/bullrocket/data/FXC.csv" "test/bullrocket/data/GLD.csv" "test/bullrocket/data/FXA.csv"
              "test/bullrocket/data/FXY.csv" "test/bullrocket/data/FXB.csv"]
          t2 "test/bullrocket/data/FXE.csv"
          f3 ["test/bullrocket/data/FXA.csv" "test/bullrocket/data/SPY.csv" "test/bullrocket/data/GLD.csv"
              "test/bullrocket/data/FXB.csv" "test/bullrocket/data/FXF.csv" "test/bullrocket/data/FXC.csv"
              "test/bullrocket/data/FXY.csv" "test/bullrocket/data/CYB.csv"]
          t3 "test/bullrocket/data/FXE.csv"
          [x1 y1] (normalize f1 t1)
          [x2 y2] (normalize f2 t2)
          [x3 y3] (normalize f3 t3)
          d1 (data :basic-dataset x1 y1)
          d2 (data :basic-dataset x2 y2)
          d3 (data :basic-dataset x3 y3)
          nn1 (make-net 3 [4 4])
          nn2 (make-net 5 [4 4])
          nn3 (make-net 8 [4 4])
          nn1 (train-nn nn1 d1 :back-prop 250 0.50)
          nn2 (train-nn nn2 d2 :back-prop 250 0.64)
          nn3 (train-nn nn3 d3 :back-prop 250 0.40)]
      (is (= true (every? true? (map #(> 0.66 (.calculateError %1 %2)) (vector nn1 nn2 nn3) (vector d1 d2 d3))))))))

(deftest baskets-with-no-correlation-test
  (testing "should indicate if basket items has *no* correlation"
    (let [f1 ["test/bullrocket/data/SPY.csv" "test/bullrocket/data/FXY.csv" "test/bullrocket/data/CYB.csv"]
          t1 "test/bullrocket/data/GLD.csv"
          f2 ["test/bullrocket/data/FXC.csv" "test/bullrocket/data/GLD.csv" "test/bullrocket/data/FXA.csv"
              "test/bullrocket/data/FXY.csv" "test/bullrocket/data/FXB.csv"]
          t2 "test/bullrocket/data/CYB.csv"
          [x1 y1] (normalize f1 t1)
          [x2 y2] (normalize f2 t2)
          d1 (data :basic-dataset x1 y1)
          d2 (data :basic-dataset x2 y2)
          nn1 (make-net 3 [4 4])
          nn2 (make-net 5 [4 4])
          nn1 (train-nn nn1 d1 :back-prop 200 0.50)
          nn2 (train-nn nn2 d2 :back-prop 200 0.64)]
      (is (= true (every? true? (map #(< 0.90 (.calculateError %1 %2)) (vector nn1 nn2) (vector d1 d2))))))))
