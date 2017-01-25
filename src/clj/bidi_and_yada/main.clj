(ns bidi-and-yada.main
  (:require [com.stuartsierra.component :as component]
            [bidi-and-yada.system :as system]
            [bidi-and-yada.config :as config])
  (:gen-class :main true))

(defn print-usage
  []
  (println (str "Usage: java -jar bidi-and-yada.jar server [env]"))
  (println "  env = environment key (dev, test, or production)"))

(defn -main
  [& args]
  (if (= 1 (count args))
    (let [environment (keyword (first args))]
      (component/start-system
       (system/system
        (config/app-config environment))))
    (print-usage)))
