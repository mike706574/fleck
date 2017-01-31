(ns bidi-and-yada.system-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clj-http.client :as http]
            [bidi-and-yada.system :as system]
            [clojure.data.json :as json]))

(def config {:id "bidi-and-yada" :port 8081})

(defmacro with-system
  [& body]
  `(let [~'system (component/start-system (system/system config))]
     (try
       ~@body
       (finally (component/stop-system ~'system)))))

(deftest saying-hello
  (with-system
    (testing "should say hello in English by default"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/hello")]
        (is (= 200 status))
        (is (= "Hello, world!\n" body))))

    (testing "should say hello in English when requested"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/hello"
                                            {:headers {"Accept-Language" "en"}
                                             :throw-exceptions false})]
        (is (= 200 status))
        (is (= "Hello, world!\n" body))))

    (testing "should say hello in Italian when requested"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/hello"
                                            {:headers {"Accept-Language" "it-it"}
                                             :throw-exceptions false})]
        (is (= 200 status))
        (is (= "Buongiorno, mondo!\n" body))))

    (testing "should say hello in Japanese when requested"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/hello"
                                            {:headers {"Accept-Language" "ja-jp"}
                                             :throw-exceptions false})]
        (is (= 200 status))
        (is (= "Konnichiwa sekai!\n" body))))

    (testing "should reject unsupported language"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/hello"
                                            {:headers {"Accept-Language" "wat"}
                                             :throw-exceptions false})]
        (is (= 406 status))
        (is (= "\r\n\r\n{:status 406}\n" body))))

    (testing "should have swagger"
      (let [{:keys [status body]} (http/get "http://localhost:8081/api/swagger.json"
                                            {:throw-exceptions false})]
        (is (= 200 status))
        (is (= {"swagger" "2.0",
                "info"
                {"title" "Hello API" "version" "1.0" "description" "An API"}
                "produces" ["application/json"]
                "consumes" ["application/json"]
                "paths"
                {"/hello"
                 {"get"
                  {"produces" ["text/plain"]
                   "responses" {"default" {"description" ""}}}}}
                "basePath" "/api"
                "definitions" {}}
               (json/read-str body)))))))
