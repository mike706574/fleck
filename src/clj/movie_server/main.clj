(ns movie-server.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [movie-server.system :as system])
  (:gen-class :main true))

(def id "movie-server")

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (log/info (str "Using port " port "."))
    (component/start-system
     (system/system {:id "movie-server" :port port}))
    @(promise)))
