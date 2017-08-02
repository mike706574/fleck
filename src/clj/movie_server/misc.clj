(ns movie-server.misc
  (:require [clojure.string :as str]
            [movie-server.misc :as misc]
            [taoensso.timbre :as log]))

(defn pretty [form] (with-out-str (clojure.pprint/pprint form)))

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

(defn dashed-keyword
  [s]
  (keyword (str/replace s #"_" "-")))

(defn with-retry
  ([opts retry? operation]
   (retry opts retry? operation identity))
  ([opts retry? operation on-success]
   (retry opts retry? operation on-success (constantly nil)))
  ([opts retry? operation on-success on-failure]
   (let [{:keys [initial-wait wait-increase max-attempts]
          :or {initial-wait 0 wait-increase 100 max-attempts 10}} opts]
     (loop [i 1 wait initial-wait]
       (let [output (operation)]
         (log/trace (str "Attempt " i " output: " (misc/pretty output)))
         (if (retry? output)
           (if (> i max-attempts)
             (do (log/error (str "Failed after " max-attempts" attempts."))

                 (on-failure output))
             (do (log/warn (str "Attempt " i " of " max-attempts " failed. Sleeping for " wait " ms."))
                 (Thread/sleep wait)
                 (recur (inc i) (+ wait wait-increase))))
           (on-success output)))))))
