(ns fleck.store
  (:require [fleck.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]))

(def alphabet (mapv (comp str char) (range 97 123)))

(defn- string-or-file?
  [x]
  (or (string? x)
      (instance? java.io.File x)))

(defn file-type?
  [exts file]
  {:pre [(seq exts)
         (every? string? exts)
         (string-or-file? file)]}
  (let [path (io/absolute file)]
    (boolean (some (fn [ext] (.endsWith path ext)) exts))))

(def video-exts [".mkv" ".avi" ".mp4" ".m4v" ".wmv"])
(def video? (partial file-type? video-exts))

(def subtitles-exts [".srt"])
(def subtitles? (partial file-type? subtitles-exts))

(defn classify-file
  [file]
  {:pre (string-or-file? file)}
  (cond
    (video? file) :video
    (subtitles? file) :subtitles
    :else :other))

(defn parse-item
  [dir]
  {:pre (io/directory? dir)}
  (let [movie-name (io/base dir)
        dir-path (io/absolute dir)
        {:keys [video subtitles unknown]} (group-by classify-file (io/list dir))]
    (merge {:name movie-name :directory dir-path}
           (case (count video)
             0 {:status :no-video-files}
             1 (let [file (io/name (first video))]
                 (case (count subtitles)
                   0 {:status :ok :file file}
                   1 {:status :ok
                      :file file
                      :subtitles (io/name (first subtitles))}
                   {:status :multiple-subtitle-files
                    :file file
                    :subtitles (map io/name subtitles)}))
             {:status :multiple-video-files
              :files (map io/absolute video)}))))

(defn parse-letter
  [root category letter]
  (let [path (str root "/" category "/" letter)]
    (if (io/exists? path)
      (->> path
           (io/list)
           (filter io/directory?)
           (map parse-item)
           (map #(assoc % :letter letter :category category)))
      [])))

(defn parse-category
  [root category]
  (flatten (map (partial parse-letter root category) alphabet)))

(defn parse-everything
  [root]
  (flatten (map (partial parse-category root) ["watched" "unwatched"])))

;;(parse-everything "/mnt/Mammoth")

(comment
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


  (defn fetch-videos
    [root]
    (let [[videos problems] (split-on (prop= :status :ok) (parse-category-dir root "unwatched"))]
      {:video videos
       :problems problems}))
  )
