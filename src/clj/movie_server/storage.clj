(ns movie-server.storage
  (:refer-clojure :exclude [load])
  (:require [movie-server.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(def alphabet ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z"])

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
                   0 {:status :ok
                      :file file
                      :subtitles nil}
                   1 {:status :ok
                      :file file
                      :subtitles (io/name (first subtitles))}
                   {:status :multiple-subtitle-files
                    :file file
                    :subtitles (map io/name subtitles)}))
             {:status :multiple-movie-files
              :files (map io/absolute movie)}))))

(defn parse-dir
  [path]
  (->> path
       (io/list)
       (filter io/directory?)
       (map parse-item)))

(defn parse-letter-directory
  [path]
  (if (io/exists? path)
    (->> path
         (io/list)
         (filter io/directory?)
         (map parse-item))
    []))

(defn parse-category-directory
  [path]
  (letfn [(letter-entry [letter]
            (let [letter-path (str path "/" (str/lower-case letter))]
              (map #(assoc % :letter letter) (parse-letter-directory letter-path))))]
    (flatten (map letter-entry alphabet))))

(defn load
  [root]
  (concat (parse-category-directory (str root "/unwatched"))
          (parse-category-directory (str root "/watched"))))
