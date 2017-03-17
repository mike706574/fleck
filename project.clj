(defproject mike/movie-server "0.0.1-SNAPSHOT"
  :description "A webservice for retrieving a movie collection."
  :url "https://github.com/mike706574/movie-server"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.taoensso/timbre "4.8.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [potemkin "0.4.3"]
                 [clj-http "3.4.1"]
                 [aleph "0.4.3"]
                 [yada "1.2.1"]
                 [bidi "2.0.16"]
                 [environ "1.1.0"]
                 [prismatic/schema "1.1.3"]]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :plugins [[lein-environ "1.1.0"]]
  :uberjar-name "movie-server.jar"
  :profiles {:uberjar {:aot :all
                       :main movie-server.main}
             :dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]}}
  :repl-options {:init-ns user})
