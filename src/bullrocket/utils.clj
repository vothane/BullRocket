(ns bullrocket.utils
  (:require [clojure.data.csv :as csv]
	    [clojure.java.io :as io]))

(defn parse-csv [file]
  (with-open [reader (io/reader file)]
    (doall (csv/read-csv reader))))
