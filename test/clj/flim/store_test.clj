(ns flim.store-test
  (:require [clojure.test :refer :all]
            [flim.io :as io]
            [clojure.zip :as zip]
            [flim.store :as domain]))

(deftest file-type?
  (is (true? (domain/file-type? [".txt"] "foo.txt")))
  (is (false? (domain/file-type? [".txt"] "foo.gif")))
  (is (true? (domain/file-type? [".txt" ".gif"] "foo.gif"))))

(deftest classify-file?
  (is (= :video (domain/classify-file "foo.avi")))
  (is (= :subtitles (domain/classify-file "foo.srt")))
  (is (= :other (domain/classify-file "foo.gif"))))

(def dirs ["a" ["b" ["b1" "b2"]] ["c" ["c1"]]])

(comment
  (deftest parse-letter-dir
    (try
      (io/touch "foo/watched/d/Danger/Duty.avi")
      (io/touch "foo/watched/d/Danger/December.gif")
      (io/touch "foo/watched/d/Danger/Danger.avi")
      (io/touch "foo/watched/d/Danger/English.srt")
      (io/touch "foo/watched/d/Danger/Spanish.srt")
      (io/touch "foo/watched/d/Dingo/Dingo.avi")
      (io/touch "foo/watched/d/Dingo/Dingo.srt")
      (io/touch "foo/watched/d/Dingo/Dingo.srt")
      (io/touch "foo/watched/d/Dog/Dog Part 1.avi")
      (io/touch "foo/watched/d/Dog/Dog Part 2.avi")
      (io/make-directory "foo/watched/d/Door")
      (domain/parse-letter-dir "foo" "watched" "d")
      (finally (io/delete-recursively "foo")))


    ))
