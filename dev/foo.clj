(ns foop
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [clojure.data.json :as json]
   [com.stuartsierra.component :as component]
   [fleck.system :as system]
   [clj-http.client :as http]
   [taoensso.timbre :as log]

   [fleck.message :refer [return accept reject]]))

(def video-root "/home/mike/sandbox/clojure/fleck/video/movie")

(def alphabet (map (comp str char) (range 97 123)))

(defn file-type?
  [exts file]
  (let [path (.getAbsolutePath file)]
    (boolean (some (fn [ext] (.endsWith path ext)) exts))))

(def video-exts [".mkv" ".avi" ".mp4"])
(def video? (partial file-type? video-exts))

(def subtitle-exts [".srt"])
(def subtitle? (partial file-type? subtitle-exts))

(def file-classes
  {:video video?
   :subtitle subtitle?})

(defn classify-file
  [file]
  (letfn [(this-class? [[k f]]
            (f file))]
    (first (first (filter this-class? file-classes)))))

(comment :classify-file
  (= :video (classify-file (io/file "ok.avi")))
  (= :subtitle (classify-file (io/file "ok.srt")))
  (nil? (classify-file (io/file "foo"))))

(defn parse-item
  [category file]
  (log/debug (str "Processing " (.getAbsolutePath file) "."))
  (let [path (.getAbsolutePath file)]
    (if (.isFile file)
      (let [video? (video? file)]
        (return (if video? :ok :not-a-video)
                {:path path :category category}
                (str path " is" (when-not video? " not ") "a video.")))
      (let [items (.listFiles file)
            {:keys [video subtitle]} (group-by classify-file items)]
        (if (= (count video) 1)
          (accept {:path (.getAbsolutePath (first video))
                   :category category
                   :subtitles (mapv #(.getAbsolutePath %) subtitle)}
                  (str "Found single video."))
          (return :multiple-videos
                  {:path path
                   :videos video}
                  "Multiple videos found."))))))

(defn parse-letter
  [root category letter]
  (let [path (str root "/" category "/" letter)
        letter-file (io/file path)]
    (if-not (.exists letter-file)
      (accept [] (str "Letter directory " path " not found."))
      (let [key-category (keyword category)
            parse-item (partial parse-item category)
            listing (.listFiles letter-file)]
        (mapv parse-item listing)))))

(parse-letter video-root "unwatched" "c")
(doall (parse-letter video-root "unwatched" "d"))
(defn parse
  [root sub]
  (let [parse-letter (partial parse-letter root sub)]
    nil))

(comment :scratch
  (http/get "http://localhost:8080/api/hello" {:throw-exceptions false})
  (json/read-str (:body (http/get "http://localhost:8080/api/swagger.json" {:throw-exceptions false})))





  )
