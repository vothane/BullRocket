(ns bullrocket.Models.kmeans
  (:require [incanter.core :as i]
            [incanter.io]
            [incanter.zoo :as zoo]
            [clj-time.format :as tf]))

(def ^:dynamic *formatter* (tf/formatter "dd-MMM-yy"))
(defn parse-date [date] (tf/parse *formatter* date))

(def data
  (i/add-derived-column
    :date [:date-str] parse-date
    (i/col-names
      (incanter.io/read-dataset data-file)
      [:date-str :open :high :low :close :volume])))

(def data-zoo (zoo/zoo data :date))

(def data-roll5
  (->>
    (i/sel data-zoo :cols :close)
    (zoo/roll-mean 5)
    (i/dataset [:five-day])
    (i/conj-cols data-zoo)))
