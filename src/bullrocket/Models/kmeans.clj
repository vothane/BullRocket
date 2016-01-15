(ns bullrocket.Models.kmeans
  (:require [incanter.core :as i]
            [incanter.io]
            [incanter.zoo :as zoo]
            [incanter.stats :as s]
            [clj-time.format :as tf]))

(def ^:dynamic *formatter* (tf/formatter "dd-MMM-yy"))
(defn parse-date [date] (tf/parse *formatter* date))

(defn get-data [data-file]
  (i/add-derived-column
    :date [:date-str] parse-date
    (i/col-names (incanter.io/read-dataset data-file :header true) [:date-str :open :high :low :close :volume :adj-close])))

(defn data-zoo [data-file]
  (zoo/zoo (get-data data-file) :date))

(defn data-roll80 [data-file]
  (let [data (data-zoo data-file)]
    (->> (i/sel data :cols :close)
         (zoo/roll-mean 60)
         (i/dataset [:60day])
         (i/conj-cols data))))