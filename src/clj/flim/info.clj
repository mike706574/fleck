(ns flim.info
  (:require [clojure.string :as str]
            [clj-http.client :as http]
            [clojure.data.json :as json]))


(defn dashed-keyword
  [s]
  (keyword (str/replace s #"_" "-")))

(defn search-movie
  [query]
  (println (str "Searching for \"" query "\"."))
  (let [{:keys [status body] :as response}
        (http/get "https://api.themoviedb.org/3/search/movie"
                  {:query-params {"query" query
                                  "api_key" "7197608cef1572f5f9e1c5b184854484"}
                   :headers {"Content-Type" "application/json;charset=utf8"}
                   :throw-exceptions false})]
    (case status
      200 {:status :ok :body (json/read-str body :key-fn dashed-keyword)}
      404 {:status :not-found :body body}
      {:status :error :body body})))


(defn ellipsis
  [length string]
  (if (> (count string) length)
    (str (str/trim (str/join (take length string))) "...")
    string))

(defn tweak-info
  [{:keys [overview] :as movie}]
  (-> movie
      (select-keys [:title :release-date :overview])
      (assoc :shortened-overview (ellipsis 100 overview))))

(defn parse-response
  [response]
  (let [{:keys [status body]} response]
    (if-not (= status :ok)
      {:status :info-error :body body}
      (if-let [info (first (:results body))]
        {:status :ok :body (tweak-info info)}
        {:status :info-not-found :body body}))))


(def retrieve (comp parse-response search-movie))
