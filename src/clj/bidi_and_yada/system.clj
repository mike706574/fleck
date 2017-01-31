(ns bidi-and-yada.system
  (:require [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [bidi-and-yada.service :as service]))

(defn hello-routes
  []
  ["/hello" (yada/resource
             {:methods
              {:get
               {:produces
                {:media-type "text/plain"
                 :language #{"en" "ja-jp;q=0.9" "it-it;q=0.9"}}
                :response (fn [request]
                            (log/info "Saying hello!")
                            (case (yada/language request)
                              "en" "Hello, world!\n"
                              "it-it" "Buongiorno, mondo!\n"
                              "ja-jp" "Konnichiwa sekai!\n"))}}})])

(defn routes
  []
  ["/api" (-> (hello-routes)
              (yada/swaggered
               {:info {:title "Hello API"
                       :version "1.0"
                       :description "An API"}
                :basePath "/api"})
              (bidi/tag :hello.resources/api))])

(defn system
  [config]
  {:app (service/yada-service config (routes))})
