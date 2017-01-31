(ns fleck.main
  (:require [com.stuartsierra.component :as component]
            [fleck.system :as system])
  (:gen-class :main true))

(def id "fleck")
(def port 8080)
(def config {:id id :port port})

(defn -main
  [& args]
  (if (= (count args) 0)
    (do (component/start-system (system/system config))
        @(promise))
    (println (str "Usage: java -jar " id ".jar server"))))
