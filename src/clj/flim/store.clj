(ns flim.store
  (:refer-clojure :exclude [load])
  (:require [flim.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]))

(def alphabet (mapv (comp str char) (range 97 123)))

(defn file-type?
  [exts file]
  {:pre [(seq exts)
         (every? string? exts)]}
  (let [path (io/absolute file)]
    (boolean (some (fn [ext] (.endsWith path ext)) exts))))

(def movie-exts [".mkv" ".avi" ".mp4" ".m4v" ".wmv"])
(def movie? (partial file-type? movie-exts))

(def subtitles-exts [".srt"])
(def subtitles? (partial file-type? subtitles-exts))

(defn classify-file
  [file]
  (cond
    (movie? file) :movie
    (subtitles? file) :subtitles
    :else :other))

(defn parse-item
  [dir]
  {:pre (io/directory? dir)}
  (let [{:keys [movie subtitles unknown]} (group-by classify-file (io/list dir))]
    (merge {:title (io/base dir)
            :directory (io/absolute dir)}
           (case (count movie)
             0 {:status :no-movie-files}
             1 (let [file (io/name (first movie))]
                 (case (count subtitles)
                   0 {:status :ok :file file}
                   1 {:status :ok
                      :file file
                      :subtitles (io/name (first subtitles))}
                   {:status :multiple-subtitle-files
                    :file file
                    :subtitles (map io/name subtitles)}))
             {:status :multiple-movie-files
              :files (map io/absolute movie)}))))

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

(defn load
  [root]
  (flatten (map (partial parse-category root) ["watched" "unwatched"])))
