(ns bidi-and-yada.config
  (:require [clojure.string :refer [upper-case]]
            [taoensso.timbre :as log]))

(defn thread-name-output-fn
  ([data] (thread-name-output-fn nil data))
  ([opts data]
   (let [{:keys [no-stacktrace? stacktrace-fonts]} opts
         {:keys [level ?err #_vargs msg_ ?ns-str ?file hostname_
                 timestamp_ ?line]} data]
     (str
      (force timestamp_) " "
      (force hostname_) " "
      (upper-case (name level))  " "
      "[" (or ?ns-str ?file "?") ":" (or ?line "?") "] ["
      (.getName (Thread/currentThread)) "] - "
      (force msg_)
      (when-not no-stacktrace?
        (when-let [err ?err]
          (str "\n" (log/stacktrace err opts))))))))

(defn logging-config
  [environment]
  {:level :debug
   :output-fn thread-name-output-fn})

(def environments
  {:dev {:id "bidi-and-yada"
         :port 8054}
   :test {:id "bidi-and-yada"
          :port 8054}
   :production {:id "bidi-and-yada"
                :port 8054}})

(defn app-config
  [environment]
  (get environments environment))
