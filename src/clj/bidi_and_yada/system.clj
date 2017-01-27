(ns bidi-and-yada.system
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [bidi-and-yada.config :refer [app-config logging-config]]
            [yada.yada :as yada]
            [bidi-and-yada.service :as service]
            ))

(def routes
  [""
   [["/hello"
     (yada/resource
      {:methods
       {:get
        {:produces
         {:media-type "text/plain"

          :language #{"en" "ja-jp;q=0.9" "it-it;q=0.9"}}
         :response (fn [request]
                     (case (yada/language request)
                       "ja-jp" "Konnichiwa sekai!\n"
                       "it-it" "Buongiorno, mondo!\n"
                       "en" "Hello world!\n"))}}})]
    [true (yada/handler nil)]]])

(defn system
  [config]
  {:app (service/yada-service config routes)})
