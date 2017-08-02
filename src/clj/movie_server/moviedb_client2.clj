(ns movie-server.moviedb-client2
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [movie-server.misc :as misc]
            [taoensso.timbre :as log]
            [clj-http.client :as http]))

(defn retry? [response] (= (:status response) 429))

(defprotocol MovieClient
  (get-movie [this id]))

(defrecord TMDbMovieClient [url api-key retry-options]
  MovieClient
  (get-movie [this id]
    (let [url (str url "/movie/" id)]
      (log/debug (str "Getting movie with identifier \"" id " from " url "."))
      (misc/with-retry retry-options
        retry?
        #(http/get url
          {:query-params {"api_key" api-key}
           :headers {"Content-Type" "application/json;charset=utf8"}
           :throw-exceptions false})
        #(let [{:keys [status body] :as response} %]
           (case status
             200 {:status :ok :body (json/read-str body :key-fn misc/dashed-keyword)}
             404 {:status :not-found :body body}
             {:status :error :body body}))
        (constantly {:status :retry-exhaustion})))))

(defn movie-client
  [{:keys [movie-api-url movie-api-key movie-api-retry-options]}]
  (map->TMDbMovieClient {:url movie-api-url
                         :api-key movie-api-key
                         :retry-options movie-api-retry-options}))

(def config {:movie-api-url "https://api.themoviedb.org/3"
             :movie-api-key "foo"
             :movie-api-retry-options {:initial-wait 1000
                                       :wait-increase 100
                                       :max-attempts 3}})

(comment
  (def client (movie-client config))
  (get-movie client 2)

  )
