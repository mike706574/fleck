(ns movie-server.misc
  (:require [clojure.string :as str]))

(defn split-on
  [pred coll]
  (reduce
   (fn [[yes no] item]
     (if (pred item)
       (list (conj yes item) no)
       (list yes (conj no item))))
   (list (list) (list))
   coll))

(defn mapback
  [f coll]
  (into (empty coll) (map f coll)))

(defn fmap
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defn ellipsis
  [length string]
  (if (> (count string) length)
    (str (str/trim (str/join (take length string))) "...")
    string))
