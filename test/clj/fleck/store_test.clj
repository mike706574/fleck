(ns fleck.store-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [fleck.store :as domain]))

(defn make-dir
  [file]
  (.mkdir (io/file file)))

(deftest file-type?
  (is (true? (domain/file-type? [".txt"] "foo.txt")))
  (is (false? (domain/file-type? [".txt"] "foo.gif")))
  (is (true? (domain/file-type? [".txt" ".gif"] "foo.gif"))))

(deftest classify-file?
  (is (= :video (domain/classify-file "foo.avi")))
  (is (= :subtitles (domain/classify-file "foo.srt")))
  (is (= :other (domain/classify-file "foo.gif"))))

;; (domain/parse-letter-dir video-root "unwatched" "d")

(defn delete-recursively
  [file]
  (doseq [child (reverse (file-seq (io/file file)))]
    (io/delete-file child)))

(def dirs ["a" ["b" ["b1" "b2"]] ["c" ["c1"]]])

(defn make-file
  [file]
  (io/make-parents file)
  (.createNewFile (io/file file)))

(comment
  (deftest parse-letter-dir
    (try
      (make-file "foo/watched/d/Danger/Duty.avi")
      (make-file "foo/watched/d/Danger/December.gif")
      (make-file "foo/watched/d/Danger/Danger.avi")
      (make-file "foo/watched/d/Danger/English.srt")
      (make-file "foo/watched/d/Danger/Spanish.srt")
      (make-file "foo/watched/d/Dingo/Dingo.avi")
      (make-file "foo/watched/d/Dingo/Dingo.srt")
      (make-file "foo/watched/d/Dingo/Dingo.srt")
      (make-file "foo/watched/d/Dog/Dog Part 1.avi")
      (make-file "foo/watched/d/Dog/Dog Part 2.avi")
      (make-dir "foo/watched/d/Door")
      (domain/parse-letter-dir "foo" "watched" "d")
      (finally (delete-recursively "foo")))


    ))
