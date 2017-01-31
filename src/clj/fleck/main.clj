(ns fleck.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [fleck.system :as system])
  (:gen-class :main true))

(def id "fleck")

(defn -main
  [& [port]]
  (let [port (or port (env :port) 5000)]
    (component/start-system
     (system/system {:id "fleck" :port port}))
    @(promise)))
