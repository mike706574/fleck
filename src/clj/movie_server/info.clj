(ns movie-server.info
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [movie-server.date :as date]
            [movie-server.moviedb-client :as moviedb]))

(def api-key "7197608cef1572f5f9e1c5b184854484")

(defn get-info
  [title]
  (let [{:keys [status body] :as response} (moviedb/search-movie api-key title)]
    (if-not (= status :ok)
      {:status :error :body response}
      ;; TODO: Handle multiple results. Filter by title.
      (if-let [info (first (:results body))]
        (let [{:keys [status body] :as response} (moviedb/get-movie api-key (:id info))]
          (if (= status :ok)
            {:status :ok :body (merge info body)}
            {:status :error :body response}))
        {:status :not-found :body body}))))

(defn handle-date
  [date]
  (when-not (str/blank? date)
    (date/translate "yyyy-MM-dd" "MMMM dd, yyyy" date)))

(defn shorten
  [length string]
  (if (> (count string) length)
    (let [truncated (str/join (take length string))
          last-space-index (.lastIndexOf truncated " ")
          end-index (if (neg? last-space-index)
                      (count truncated)
                      last-space-index)
          spaced (subs truncated 0 end-index)
          end (if (str/ends-with? spaced ".")
                ""
                "...")]
      (str (str/trim spaced) end))))

(defn process-info
  [info]
  (-> info
      (set/rename-keys {:title :tmdb-title
                        :id :tmdb-id})
      (update :release-date handle-date)
      (update :overview (partial shorten 150))
      (select-keys [:title
                    :tmdb-title
                    :tmdb-id
                    :imdb-id
                    :release-date
                    :overview
                    :backdrop-path])))
