(ns foo
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
   [taoensso.timbre :as log]))

(def video-root "/home/mike/video/movie")

(def alphabet (map (comp str char) (range 97 123)))

(defmacro functionize [macro]
  `(fn [& args#] (eval (cons '~macro args#))))

(def ^:dynamic *report-fn* (fn [args] (apply (functionize log/debug) args)))

(defn video?
  [item]
  ;; TODO: Bad!
  (let [path (.getAbsolutePath item)
        video? (.endsWith path ".avi")]
    (log/debug (str "Is " path " a video? " video?))
    video?))

(defn report
  [value & args]
  (*report-fn* args)
  value)

(defn parse-item
  [file]
  (log/debug (str "Processing " (.getAbsolutePath file) "."))
  (if (.isFile file)
    (let [path (.getAbsolutePath file)]
      (log/debug (str path " is a file."))
      {:path path})
    (let [items (.listFiles file)
          videos (filter video? items)]
      (if (= (count videos) 1)
        {:path (.getAbsolutePath (first videos))}
        (throw (ex-info (str "WHAT!") {:videos videos}))))))

(defn parse-letter
  [root category letter]
  (let [path (str root "/" category "/" letter)
        letter-file (io/file path)]
    (if-not (.exists letter-file)
      (report [] (str "Letter directory " path " not found."))
      (let [key-category (keyword category)
            listing (.listFiles letter-file)]
        (println listing)
        (->> listing
             (map parse-item)
             (map #(assoc % :category key-category)))))))

(parse-letter video-root "unwatched" "c")
(parse-letter video-root "unwatched" "d")

(defn parse
  [root sub]
  (let [parse-letter (partial parse-letter root sub)]
    nil))

(comment :scratch
  (http/get "http://localhost:8080/api/hello" {:throw-exceptions false})
  (json/read-str (:body (http/get "http://localhost:8080/api/swagger.json" {:throw-exceptions false})))





  )
