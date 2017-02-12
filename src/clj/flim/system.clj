(ns flim.system
  (:require [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [yada-component.core :as yada-component]
            [flim.store :as store]))

(def movie-root "/mnt/Mammoth")

(defn routes
  [state]
  ["" [["/movies" (yada/resource
                   {:access-control {:allow-origin "*"}
                    :methods
                    {:get
                     {:produces
                      {:media-type #{"application/edn" "application/json"}
                       :language #{"en"}}
                      :response (fn [request]
                                  (if (nil?
                                       ))
                                  (log/info "Returning movies!")
                                  (store/parse-everything movie-root))}}})]
       [true (yada/handler nil)]]])

(defn system
  [config]
  {:app (yada-component/yada-service config (routes (atom nil)))})
