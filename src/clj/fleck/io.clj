(ns fleck.io
  (:require ;;[clojure.java.io :as io]
            [potemkin :refer [import-vars]]))

(potemkin/import-vars
  [clojure.java.io
    file
    copy])

(defn name
  [arg]
  (.getName (file arg)))

(defn base
  [arg]
  (let [name (.getName (file arg))
        dot-index (.lastIndexOf name ".")]
    (if (pos? dot-index) (subs name 0 dot-index) name)))

(defn extension
  [arg]
  (let [name (.getName (file arg))
        dot-index (.lastIndexOf name ".")]
    (when (pos? dot-index)
      (subs name dot-index))))

(defn absolute
  [arg]
  (.getAbsolutePath (file arg)))

(defn parent
  [arg]
  (.getParent (file arg)))

(defn file?
  [arg]
  (.isFile (file arg)))

(defn directory?
  [arg]
  (.isDirectory (file arg)))

(defn exists?
  [arg]
  (.exists (file arg)))

(defn list
  [arg]
  (.listFiles (file arg)))
