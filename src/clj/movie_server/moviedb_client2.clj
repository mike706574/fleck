(ns movie-server.moviedb-client2
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [movie-server.misc :as misc]
            [taoensso.timbre :as log]
            [clj-http.client :as http]))

(defn with-retry
  [retry-options retryable-statuses execute-request handle-response]
  (let [{:keys [initial-wait wait-increase max-attempts]} retry-options]
    (loop [i 1 wait initial-wait]
      (let [{:keys [status body] :as response} (execute-request)]
        (if (retryable-statuses status)
          (if (> i max-attempts)
            (do (log/error (str "Rejected after " max-attempts" attempts."))
                {:status :retry-exhaustion})
            (do (log/error (str "Attempt " i " of " max-attempts " rejected. Sleeping for " wait " ms."))
                (Thread/sleep wait)
                (recur (inc i) (+ wait wait-increase))))
          (handle-response response))))))

(def ^:dynamic *get-retry-options* {:initial-wait 0
                                    :wait-increase 200
                                    :max-attempts 20})

(defn get-movie
  [api-key id]
  (log/debug (str "Getting movie with identifier \"" id "\"."))
  (with-retry *get-retry-options* #{429}
    #(http/get (str "https://api.themoviedb.org/3/movie/" id)
               {:query-params {"api_key" api-key}
                :headers {"Content-Type" "application/json;charset=utf8"}
                :throw-exceptions false})
    #(let [{:keys [status body] :as response} %]
       (case status
         200 {:status :ok :body (json/read-str body :key-fn misc/dashed-keyword)}
         404 {:status :not-found :body body}
         {:status :error :body body}))))


(def api-key "7197608cef1572f5f9e1c5b184854484")

(comment
  (get-movie api-key "2")

  )
