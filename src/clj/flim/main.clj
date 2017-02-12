(ns flim.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [flim.system :as system])
  (:gen-class :main true))

(def id "flim")

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (log/info (str "Using port " port "."))
    (component/start-system
     (system/system {:id "flim" :port port}))
    @(promise)))
