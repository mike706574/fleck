(ns flim.misc)

(defn- split-on
  [pred coll]
  (reduce
   (fn [[yes no] item]
     (if (pred item)
       (list (conj yes item) no)
       (list yes (conj no item))))
   (list (list) (list))
   coll))

(defn- prop= [k v]
  (fn [map] (= (get map k) v)))
