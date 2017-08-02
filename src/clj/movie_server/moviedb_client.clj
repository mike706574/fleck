(ns movie-server.moviedb-client2
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [movie-server.misc :as misc]
            [taoensso.timbre :as log]
            [clj-http.client :as http]))

(defn ^:private retry-statuses
  [statuses]
  (fn retry? [response]
    (contains? (set statuses) (:status response))))

(defn ^:private get-request
  [url query-params retry-options]
  (let [{:keys [status body]} (misc/with-retry
                                (fn execute-request []
                                  (http/get url
                                            {:query-params query-params
                                             :headers {"Content-Type" "application/json;charset=utf8"}
                                             :throw-exceptions false}))
                                (retry-statuses #{429})
                                (fn next-wait [wait] (+ wait 100))
                                retry-options)]
    (case status
      200 {:status :ok :body (json/read-str body :key-fn misc/dashed-keyword)}
      429 {:status :retry-exhaustion}
      404 {:status :not-found :body body}
      {:status :error :body body})))

(defprotocol MovieClient
  (get-config [this])
  (get-movie [this id])
  (search-movies [this query]))

(defrecord TMDbMovieClient [url api-key retry-options]
  MovieClient
  (get-config [this]
    (let [url (str url "/configuration")]
      (get-request url {"api_key" api-key} retry-options)))

  (get-movie [this id]
    (let [url (str url "/movie/" id)]
      (log/debug (str "Getting movie with identifier \"" id " from " url "."))
      (get-request url {"api_key" api-key} retry-options)))

  (search-movies [this query]
    (let [url (str url "/search/movie")]
      (get-request url {"query" query "api_key" api-key} retry-options))))

(defn movie-client
  [{:keys [movie-api-url movie-api-key movie-api-retry-options]}]
  (map->TMDbMovieClient {:url movie-api-url
                         :api-key movie-api-key
                         :retry-options movie-api-retry-options}))
