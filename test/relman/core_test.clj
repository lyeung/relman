(ns relman.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [relman.core :refer :all]))

;;(deftest printUsage-test
;;  (printUsage))

(def tmpDir (str (System/getProperty "java.io.tmpdir") "/"))

(deftest rootWarFilename-test
  (is (= "ROOT.war" rootWarFilename)))

(deftest containsPrefix-test
  (is (true? (containsPrefix "quick brown fox" "quick"))))

(deftest not-containsPrefix-test
  (is (false? (containsPrefix "quick brown fox" "quik"))))

(deftest getSuffixIndex-test
  (is (= 12 (getSuffixIndex "quick brown fox" "fox"))))

(deftest containsSuffix-test
  (is (true? (containsSuffix "quick brown fox" "fox"))))

(deftest not-containsSuffix-test
  (is (false? (containsSuffix "quick brown fox" "fix"))))

(deftest artifactPredicate-test
  (is (true? (artifactPredicate "feature-" "#100" (io/file "feature-xxx-#100")))))

(deftest artifactPredicate-unmatch-prefix-test
  (is (false? (artifactPredicate "develop" "#100" (io/file "feature-xxx-#100")))))

(deftest artifactPredicate-unmatch-suffix-test
  (is (false? (artifactPredicate "feature-" "#101" (io/file "feature-xxx-#100")))))

(deftest findArtifact-test
  (let [artifacts [(io/file "feature-xxx-#200")
                  (io/file "feature-xxx-#101")
                  (io/file "feature-xxx-#100")]
       result (findArtifact artifacts "feature-" "-#100")]
  (is (= 1 (count result)))
  (is (= "feature-xxx-#100" (.getName (first result))))))

(deftest copyToRootWarFilename-test
  (let [currentMillis (tc/to-long (time/now))
       sourceFile (str tmpDir "sourceFile-" currentMillis ".war")
       artifactFile (io/file sourceFile)
       targetFile (str tmpDir "ROOT.war")]
    (io/delete-file targetFile true)
    (with-open [writer (io/writer sourceFile)]
      (.write writer ""))
    (copyToRootWarFilename artifactFile)
    (is (true? (.exists (io/file targetFile))))))

(deftest listFiles-test
  (let [list ["feature-xxx-#100" "feature-xxx-#101" "feature-xxx-#200"]]
    ;; create test files
    (doseq [e list]
      (with-open [writer (io/writer (str tmpDir e))]
        (.write writer "")))
    (is (= "feature-xxx-#101" (.getName (first (listFiles "feature-" "#101" "/tmp")))))
    (is (= "feature-xxx-#100" (.getName (first (listFiles "feature-" "#100" "/tmp")))))
    (is (= "feature-xxx-#200" (.getName (first (listFiles "feature-" "#200" "/tmp")))))
    ))

;;(deftest a-test
;;  (testing "FIXME, I fail."
;;    (is (= 0 1))))