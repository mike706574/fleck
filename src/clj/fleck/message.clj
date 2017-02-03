(ns fleck.message
  (:require [taoensso.timbre :as log]))

;; TODO: All macros. All the time.

(def ^:dynamic *return-hook*
  (fn [_ _ message]
    (when message (log/debug message))))

(defn return
  ([status value]
   (return status value nil))
  ([status value message]
   (*return-hook* status value message)
   [status value]))

(def ^:dynamic *accept-hook*
  (fn [_ message]
    (when message) (log/debug message)))

(defn accept
  ([value]
   (accept value nil))
  ([value message]
   (*accept-hook* value message)
   [:ok value]))

(def ^:dynamic *reject-hook*
  (fn [_ message]
    (when message (log/error message))))

(defn reject
  ([value]
   (reject value nil))
  ([value message]
   (*reject-hook* value message)
   [:error value]))
