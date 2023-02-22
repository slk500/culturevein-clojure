(ns frontend.formatter
  (:require
   [goog.string :refer [format]]))

(defn seconds-to-time-string [seconds]
  (let [minutes (quot seconds 60)
        seconds (mod seconds 60)]
    (str (format "%2d" minutes) ":" (format "%02d" seconds))))
