(ns moop.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [moop.config :refer [app-config logging-config]]
            [yada.yada :as yada])
  (:gen-class :main true))

(defrecord YadaWebservice [id port routes server]
  component/Lifecycle
  (start [this]
    (if server
      (do (println "Already started.")
          this)
      (do (println (str "Starting " id " on port " port "..."))

          (let [this (assoc this :server
                            (aleph.http/start-server
                             (bidi.ring/make-handler routes)
                             {:port port}))]
            (println (str "Finished starting."))
            this))))
  (stop [this]
    (if server
      (do (println (str "Stopping " id " on port " port "..."))
          (.close server)
          (assoc this :server nil))
      (do (println "Already stopped.")
          this))))

(defn yada-webservice
  [{:keys [port] :as config} routes]
  {:pre [(integer? port)
         (> port 0)]}
  (component/using (map->YadaWebservice (assoc config :routes routes)) []))

(def routes
  [""
   [["/hello" (yada/handler "Hello World!\n")]
    ["/hello-language"
     (yada/resource
      {:methods
       {:get
        {:produces
         {:media-type "text/plain"
          :language #{"en" "zh-ch;q=0.9"}}
         :response (fn [request]
                     (case (yada/language request)
                       "zh-ch" "你好世界\n"
                       "en" "Hello World!\n"))}}})]
    [true (yada/handler nil)]]])

(defn system [config]
  {:app (yada-webservice config routes)})

(defn print-usage
  []
  (println (str "Usage: java -jar moop.jar server [env]"))
  (println "  env = environment key (dev, test, or production)"))

(defn -main
  [& args]
  (if (= 1 (count args))
    (let [config (app-config (keyword (first args)))]
      (component/start-system (system config)))
    (print-usage)))
