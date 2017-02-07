(ns fleck.system
  (:require [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [yada-component.core :as yada-component]
            [fleck.store :as store]))

(def video-root "/home/mike/sandbox/clojure/fleck/video/movie")

(defn video-routes
  []
  ["/hello" (yada/resource
              {:access-control
               {:allow-origin "*"
                :allow-credentials false
                :expose-headers #{"X-Custom"}
                :allow-methods #{:get :post}
                :allow-headers ["Api-Key"]}
              :methods
              {:get
               {:produces
                {:media-type #{"text/plain"}
                 :language #{"en"}}
                :response (fn [request]
                            (log/info "Saying hello!")
                            "Hello")}}})
   "/videos" (yada/resource
              {:access-control
               {:allow-origin "*"
                :allow-credentials false
                :expose-headers #{"X-Custom"}
                :allow-methods #{:get :post}
                :allow-headers ["Api-Key"]}
              :methods
              {:get
               {:produces
                {:media-type #{"application/edn" "application/json"}
                 :language #{"en"}}
                :response (fn [request]
                            (log/info "Returning videos!")
                            (store/parse-category-dir video-root "unwatched"))}}})])

(defn routes
  []
  ["/api" (-> (video-routes)
              (yada/swaggered
               {:info {:title "Hello API"
                       :version "1.0"
                       :description "An API"}
                :basePath "/api"})
              (bidi/tag :hello.resources/api))
   true (yada/handler nil)])

(defn system
  [config]
  {:app (yada-component/yada-service config (routes))})
