(ns fleck.store
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]))

(def alphabet (mapv (comp str char) (range 97 123)))

(defn- string-or-file?
  [x]
  (or (string? x)
      (instance? java.io.File x)))

(defn- absolute-path
  [file]
  {:pre [(string-or-file? file)]}
  (.getAbsolutePath (io/file file)))

(defn file?
  [file]
  {:pre (string-or-file? file)}
  (.isFile (io/file file)))

(defn list-files
  [file]
  {:pre (string-or-file? file)}
  (vec (.listFiles (io/file file))))

(defn exists?
  [file]
  {:pre (string-or-file? file)}
  (.exists (io/file file)))

(defn file-type?
  [exts file]
  {:pre [(seq exts)
         (every? string? exts)
         (string-or-file? file)]}
  (let [path (absolute-path file)]
    (boolean (some (fn [ext] (.endsWith path ext)) exts))))

(def video-exts [".mkv" ".avi" ".mp4"])
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
  [file]
  {:pre (string-or-file? file)}
  (let [file-path (absolute-path file)]
    (log/debug (str "Processing " file-path "."))
    (if (file? file)
      (if (video? file)
        {:status :ok :video-path file-path}
        {:status :not-a-video :file-path file-path})
      (let [items (list-files file)
            {:keys [video subtitles unknown]} (group-by classify-file items)]
        (case (count video)
          0 {:status :no-videos :video-path file-path}
          1 (case (count subtitles)
              0 {:status :ok
                 :video-path (absolute-path (first video))}
              1 {:status :ok
                 :video-path (absolute-path (first video))
                 :subtitles-path (absolute-path (first subtitles))}
              {:status :multiple-subtitles
               :video-path (absolute-path (first video))
               :subtitles (mapv absolute-path subtitles)})
          {:status :multiple-videos
           :directory-path file-path
           :videos (mapv absolute-path video)})))))

(defn assoc-fn
  [k v]
  (fn [map] (assoc map k v)))

(defn parse-letter-dir
  [root category letter]
  (let [path (str root "/" category "/" letter)
        letter-file (io/file path)]
    (if-not (.exists letter-file)
      {:status :missing
       :directory-path (absolute-path path)}
      (let [key-category (keyword category)
            parse-item (comp (fn [item]
                               (assoc item
                                      :category category
                                      :letter letter))
                             parse-item)
            listing (.listFiles letter-file)]
        (mapv parse-item listing)))))

(defn parse-category-dir
  [root category]
  (map (partial parse-letter-dir root category) alphabet))
