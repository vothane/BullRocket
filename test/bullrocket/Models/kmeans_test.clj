(ns bullrocket.Models.kmeans-test
  (:require [clojure.test :refer :all]
            [bullrocket.Models.kmeans :refer :all]))

(def data '(2 3 5 6 10 11 100 101 102))
(def guessed-means '(0 10))

(deftest closet-test
  (testing "finding closet data point"
    (is (= (closest 2 guessed-means distance) 0 ))
    (is (= (closest 9 guessed-means distance) 10 ))
    (is (= (closest 100 guessed-means distance) 10))))

(deftest point-groups-test
  (testing "finding group of points"
    (is (= (point-groups guessed-means data distance) {0 [2 3 5], 10 [6 10 11 100 101 102]}))))

(deftest new-means-test
  (testing "finding new means"
    (is (= (new-means average (point-groups guessed-means data distance) guessed-means) '(10/3 55)))))

(deftest iterate-means-test
  (testing "iterate thru means"
    (is (= ((iterate-means data distance average) '(0 10)) '(10/3 55)))
    (is (= ((iterate-means data distance average) '(10/3 55)) '(10/3 55)))
    (is (= (take 4 (iterate (iterate-means data distance average) '(0 10))) '((0 10) (10/3 55) (37/6 101) (37/6 101))))))

(deftest k-cluster-test
  (testing "finding k-cluster"
    (is (= (k-cluster data distance '(37/6 101)) '([2 3 5 6 10 11] [100 101 102])))))

(deftest take-while-unstable-test
  (testing "take while unstable"
    (is (= (take-while-unstable '(1 2 3 4 5 6 7 7 7 7)) '(1 2 3 4 5 6 7)))
    (is (= (take-while-unstable (iterate (iterate-means data distance average)'(0 10))) '((0 10) (10/3 55) (37/6 101))))
    (is (= (take-while-unstable (map #(k-cluster data distance %) (iterate (iterate-means data distance average) '(0 10))))
           '([2 3 5 6 10 11] [100 101 102])))))

(def grouper
  (k-groups data distance average))

(deftest grouper-test
  (testing "grouping data"
    (is (= (grouper '(0 10)) '(([2 3 5] [6 10 11 100 101 102]) ([2 3 5 6 10 11] [100 101 102]))))
    (is (= (grouper '(1 2 3)) '(([2] [3 5 6 10 11 100 101 102])
                                ([2 3 5 6 10 11] [100 101 102])
                                ([2 3] [5 6 10 11] [100 101 102])
                                ([2 3 5] [6 10 11] [100 101 102])
                                ([2 3 5 6] [10 11] [100 101 102]))))
    (is (= (grouper '(0 1 2 3 4)) '(([2] [3] [5 6 10 11 100 101 102])
                                    ([2] [3 5 6 10 11] [100 101 102])
                                    ([2 3] [5 6 10 11] [100 101 102])
                                    ([2 3 5] [6 10 11] [100 101 102])
                                    ([2] [3 5 6] [10 11] [100 101 102])
                                    ([2 3] [5 6] [10 11] [100 101 102]))))
    (is (= (grouper (range 200)) '(([2] [3] [100] [5] [101] [6] [102] [10] [11]))))))


(deftest vec-average-and-vec-distance-test
  (testing "vec calcs"
    (is (= (vec-distance [1 2 3] [5 6 7]) 48))
    (is (= (vec-average  [1 2 3] [5 6 7]) '(3 4 5)))))



