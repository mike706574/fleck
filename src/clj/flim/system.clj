(ns flim.system
  (:require [clojure.string :as str]
            [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [yada-component.core :as yada-component]
            [flim.date :as date]
            [flim.info :as info]
            [flim.moviedb-client :as moviedb]
            [flim.io :as io]
            [flim.misc :as misc]
            [flim.storage :as storage]))

(defn merge-info
  [{title :title :as movie}]
  (let [{:keys [status body]} (info/get-info title)]
    (if (= status :ok)
      (merge movie (info/process-info body))
      (assoc movie :status status))))

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

(comment
  (let [movies (io/read-edn "/mnt/Mammoth/movies.edn")]
    (map (fn [movie]
           (if (nil? (:backdrop-path movie))
             (assoc movie :backdrop-path)
             )
           )
     movies)

    )

  (get-movies "/mnt/Mammoth")
  (get-movies "/mnt/Mammoth/test"))

(defn routes
  [movies]
  ["" [["/movies" (yada/resource
                   {:access-control {:allow-origin "*"}
                    :methods
                    {:get
                     {:produces
                      {:media-type #{"application/edn" "application/json"}
                       :language #{"en"}}
                      :response (fn [request]
                                  (log/info "Returning movies!")
                                  movies)}}})]
       [true (yada/handler nil)]]])

(defn system
  [config]
  (let [movies (io/read-edn "/mnt/Mammoth/movies-by-letter.edn")]
    {:app (yada-component/yada-service config (routes movies))}))
