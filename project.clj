(defproject mike/flim "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/timbre "4.8.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [potemkin "0.4.3"]
                 [aleph "0.4.1"]
                 [yada "1.2.0"]
                 [bidi "2.0.16"]
                 [environ "1.1.0"]
                 [prismatic/schema "1.1.3"]

;;                 [mike/yada-component "0.0.1-SNAPSHOT"]
                 ]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :plugins [[lein-environ "1.1.0"]]
  :uberjar-name "flim-standalone.jar"
  :profiles {:uberjar {:aot :all
                       :main flim.main}
             :dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [criterium "0.4.4"]
                                  [clj-http "3.4.1"]
                                  [org.clojure/data.json "0.2.6"]]}}
  :repl-options {:init-ns user})
