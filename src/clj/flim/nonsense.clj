(ns flim.system
  (:require  [clojure.string :as str]))

(def syllables #{"ta" "la" "ba" "ti" "do" "so" "re" "ton" "ma" "na" "da" "to"
                 "ro" "go" "ra" "te" "le"})

(defn nonsense []
  (let [syllable-count (+ 2 (rand-int 2))
        rand-syllable #(rand-nth (vec syllables))]
    (str/join (repeatedly syllable-count rand-syllable))))


(defn shuffle-middle
  [s]
  {:pre [(> (count s) 3)]}
  (str (first s) (str/join (shuffle (vec (subs s 1 (dec (count s)))))) (last s)))

(defn shuffle-rest
  [s]
  {:pre [(> (count s) 2)]}
  (str (first s) (str/join (shuffle (vec (subs s 1))))))

(shuffle-rest "remote")
