(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [clojure.data.json :as json]
   [com.stuartsierra.component :as component]
   [flim.system :as system]
   [clj-http.client :as http]))

(def config {:id "flim" :port 8000})

(defonce system nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'system (constantly (system/system config)))
  :init)

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system component/start-system)
  :started)

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop-system s))))
  :stopped)

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after `go))

(defn restart
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (go))

(comment :scratch
         (def syllables #{"ta"
                          "la"
                          "ba"
                          "ti"
                          "do"
                          "so"
                          "re"
                          "ton"
                          "ma"
                          "na"
                          "da"
                          "to"
                          "ro"
                          "go"})

         (defn nonsense []
           (let [syllable-count (+ 2 (rand-int 2))
                 rand-syllable #(rand-nth (vec syllables))]
             (str/join syllable-count (repeatedly rand-syllable))))

         #(rand-nth (vec syllables))
         (nonsense)


         (take 10 (repeatedly ))

         (take 3 (json/read-str (:body (http/get "http://192.168.1.141:8000/movies")) :key-fn keyword))

         )
