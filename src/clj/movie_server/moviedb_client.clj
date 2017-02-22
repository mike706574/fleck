(ns movie-server.moviedb-client
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [taoensso.timbre :as log]
            [clj-http.client :as http]))

(defn dashed-keyword
  [s]
  (keyword (str/replace s #"_" "-")))

(def ^:dynamic *initial-search-wait* 0)
(def ^:dynamic *search-wait-increase* 200)
(def ^:dynamic *max-search-attempts* 20)

(defn search-movie
  [api-key query]
  (log/debug (str "Searching for \"" query "\"."))
  (loop [i 1
         wait *initial-search-wait*]
    (let [{:keys [status body] :as response}
          (http/get "https://api.themoviedb.org/3/search/movie"
                    {:query-params {"query" query
                                    "api_key" api-key}
                     :headers {"Content-Type" "application/json;charset=utf8"}
                     :throw-exceptions false})]
      (log/debug (str "Got a " status "."))
      (case status
        200 {:status :ok :body (json/read-str body :key-fn dashed-keyword)}
        429 (if (> i *max-search-attempts*)
              (do (log/error (str "Search for " query " rejected after " *max-search-attempts* " attempts."))
                  {:status :retry-exhaustion})
              (do (log/error (str "Search attempt " i " of " *max-search-attempts* " rejected. Sleeping for " wait " ms."))
                  (Thread/sleep wait)
                  (recur (inc i) (+ wait *search-wait-increase*))))
        404 {:status :not-found :body body}
        {:status :error :body body}))))

(def ^:dynamic *initial-get-wait* 0)
(def ^:dynamic *get-wait-increase* 200)
(def ^:dynamic *max-get-attempts* 20)

(defn get-movie
  [api-key id]
  (log/debug (str "Getting movie with identifier \"" id "\"."))
  (loop [i 1
         wait *initial-get-wait*]
    (let [{:keys [status body] :as response}
          (http/get (str "https://api.themoviedb.org/3/movie/" id)
                    {:query-params {"api_key" api-key}
                     :headers {"Content-Type" "application/json;charset=utf8"}
                     :throw-exceptions false})]
      (log/debug (str "Got a " status "."))
      (case status
        200 {:status :ok :body (json/read-str body :key-fn dashed-keyword)}
        429 (if (> i *max-get-attempts*)
              (do (log/error (str "Get for " id " rejected after " *max-get-attempts*" attempts."))
                  {:status :retry-exhaustion})
              (do (log/error (str "Get attempt " i " of " *max-get-attempts* " rejected. Sleeping for " wait " ms."))
                  (Thread/sleep wait)
                  (recur (inc i) (+ wait *get-wait-increase*))))

        404 {:status :not-found :body body}
        {:status :error :body body}))))

(def ^:dynamic *initial-config-wait* 0)
(def ^:dynamic *config-wait-increase* 200)
(def ^:dynamic *max-config-attempts* 20)

(defn get-config
  [api-key]
  (loop [i 1
         wait *initial-config-wait*]
    (let [{:keys [status body] :as response}
          (http/get (str "https://api.themoviedb.org/3/configuration")
                    {:query-params {"api_key" api-key}
                     :headers {"Content-Type" "application/json;charset=utf8"}
                     :throw-exceptions false})]
      (log/debug (str "Got a " status "."))
      (case status
        200 {:status :ok :body (json/read-str body :key-fn dashed-keyword)}
        429 (if (> i *max-config-attempts*)
              (do (log/error (str "Attempt to retrieve config rejected after " *max-config-attempts* " attempts."))
                  {:status :retry-exhaustion})
              (do (log/error (str "Attempt to retrieve config " i " of " *max-config-attempts* " rejected. Sleeping for " wait " ms."))
                  (Thread/sleep wait)
                  (recur (inc i) (+ wait *config-wait-increase*))))
        404 {:status :not-found :body body}
        {:status :error :body body}))))
