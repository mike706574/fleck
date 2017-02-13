(ns flim.system
  (:require [taoensso.timbre :as log]
            [yada.yada :as yada]
            [bidi.bidi :as bidi]
            [yada-component.core :as yada-component]
            [flim.store :as store]
            [flim.info :as info]))

(def movie-root "/mnt/Mammoth")

(defn merge-info
  [{:keys [status title] :as movie}]
  (merge movie
         (when (= status :ok)
           (let [{:keys [status body]} (info/retrieve title)]
             (if (= status :ok)
               (assoc body :status status)
               {:status status :info-error body})))))


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
                                  (log/info "Returning movies!")
                                  (->> movie-root
                                      (store/load)
                             ;;         (map merge-info)
                                      ))}}})]
       [true (yada/handler nil)]]])

(defn system
  [config]
  {:app (yada-component/yada-service config (routes (atom nil)))})
