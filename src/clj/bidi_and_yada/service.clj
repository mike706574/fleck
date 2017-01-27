(ns bidi-and-yada.service
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [yada.yada :as yada]))

(defrecord YadaService [id port routes server]
  component/Lifecycle
  (start [this]
    (if server
      (do (log/info "Already started.")
          this)
      (do (log/info (str "Starting " id " on port " port "..."))

          (let [this (assoc this :server
                            (aleph.http/start-server
                             (bidi.ring/make-handler routes)
                             {:port port}))]
            (log/info (str "Finished starting."))
            this))))
  (stop [this]
    (if server
      (do (log/info (str "Stopping " id " on port " port "..."))
          (.close server)
          (assoc this :server nil))
      (do (log/info "Already stopped.")
          this))))

(defn yada-service
  [{:keys [port] :as config} routes]
  {:pre [(integer? port)
         (> port 0)
         (vector? routes)]}
  (component/using (map->YadaService (assoc config :routes routes)) []))
