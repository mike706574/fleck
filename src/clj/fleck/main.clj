(ns fleck.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [fleck.system :as system])
  (:gen-class :main true))

(def id "fleck")

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (log/info (str "Using port " port "."))
    (component/start-system
     (system/system {:id "fleck" :port port}))
    @(promise)))
