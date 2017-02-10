(ns fleck.system
  (:require [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [yada-component.core :as yada-component]
            [fleck.store :as store]))

(def video-root "/mnt/Mammoth")

(defn routes
  []
  ["" [["/videos" (yada/resource
                   {:access-control {:allow-origin "*"}
                    :methods
                    {:get
                     {:produces
                      {:media-type #{"application/edn" "application/json"}
                       :language #{"en"}}
                      :response (fn [request]
                                  (log/info "Returning videos!")
                                  (store/parse-everything video-root))}}})]
       [true (yada/handler nil)]]])

(defn system
  [config]
  {:app (yada-component/yada-service config (routes))})
