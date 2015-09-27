(ns bullrocket.utils-test
  (:require [clojure.test :refer :all]
            [bullrocket.utils :refer :all]))

(deftest parse-csv-test
  (testing "should parse csv"
    (let [result (parse-csv "test/bullrocket/data/FXB.csv")
          header (first result)
          data   (rest result)]
      (is (= (count data) 754))
      (is (= (first data) ["1-Apr-14" "123.71" "123.85" "123.11" "123.39" "5998286"]))
      (is (= (last data)  ["1-Apr-11" "138.63" "139.55" "137.72" "139.2" "4158264"])))))
