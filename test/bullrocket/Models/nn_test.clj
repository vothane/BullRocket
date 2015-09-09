(ns bullrocket.Models.nn-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.nn :refer :all])
  (:use     [enclog nnets training]))

(deftest parse-csv-test
  (testing "should parse csv"
    (let [f ["test/bullrocket/data/FXA.csv" "test/bullrocket/data/FXB.csv" "test/bullrocket/data/FXC.csv"]
          t "test/bullrocket/data/GLD.csv" 
          d (data f t)]
      (is (= d [])))))
