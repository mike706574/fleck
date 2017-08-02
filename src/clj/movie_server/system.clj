(ns movie-server.system
  (:require [clojure.string :as str]
            [taoensso.timbre :as log]
            [schema.core :as s]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [movie-server.date :as date]
            [movie-server.service :as service]
            [movie-server.info :as info]
            [movie-server.moviedb-client :as moviedb]
            [movie-server.io :as io]
            [movie-server.misc :as misc]
            [movie-server.storage :as storage]))

(defn merge-info
  [{title :title :as movie}]
  (let [{:keys [status body]} (info/get-info title)]
    (if (= status :ok)
      (merge movie (info/process-info body))
      (assoc movie :status status))))


(defn map-movies
  [f movies]
  (letfn [(map-letter [[letter movies]]
            [letter (map f movies)])]
    (map map-letter movies)))

(defn get-movies
  [path]
  (let [[stored-movies storage-problems] (misc/split-on #(= (:status %) :ok) (storage/load path))]
    (io/write-edn (str path "/storage-problems.edn") storage-problems)
    (let [[movies info-problems] (misc/split-on #(= (:status % :ok)) (map merge-info stored-movies))]
      (io/write-edn (str path "/info-problems.edn") info-problems)
      (let [movies-by-letter (group-by :letter movies)]
        (io/write-edn (str path "/movies.edn") movies)
        (io/write-edn (str path "/movies-by-letter.edn") movies-by-letter)
        {:movies (count movies)
         :storage-problems (count storage-problems)
         :info-problems (count info-problems)}))))

(def Movie {:status s/Keyword
            :title s/Str
            :tmdb-title s/Str
            :tmdb-id s/Int
            :imdb-id s/Str
            :directory s/Str
            :file s/Str
            :letter s/Str
            :release-date s/Str
            :overview (s/maybe s/Str)
            :backdrop-path (s/maybe s/Str)
            :subtitles (s/maybe s/Str)})

(def MoviesByLetter {s/Str [Movie]})

(comment
  (def movies (io/read-edn "/mnt/Mammoth/movies-by-letter.edn"))

  (doseq [movie (get movies "C")]
    (when-not (:release-date movie)
      (println movie)
      )
    ;;  (s/validate Movie movie)
    ))

(defn movie-resource
  [movies]
  {:access-control {:allow-origin "*"}
   :methods
   {:get {:produces {:media-type #{"application/json"}
                     :language #{"en"}}
          :response (fn [request]
                      (log/info "Returning movies!")
                      @movies)}}})

(defn routes
  [movies]
  ["" [["/movies" (yada/resource (movie-resource movies))]
       [true (yada/handler nil)]]])

(defn system
  [config]
  (log/info "Reading movies...")
  (let [movies (io/read-edn-resource "movies-by-letter.edn")]
    (log/info "Read movies!")
    {:app (service/movie-service config (routes (atom movies)))}))
