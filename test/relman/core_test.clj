(ns relman.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [relman.core :refer :all]))

;;(deftest printUsage-test
;;  (printUsage))
;;

(defn millis
  "Get current millisecond"
  []
  (tc/to-long (time/now)))

(defn tmpDir
  "Create a subdirectory from name under java.io.tmpdir"
  ([name millisecond]
    (str (System/getProperty "java.io.tmpdir") 
         "/" name "-" millisecond "/"))
  ([name] (tmpDir name (str millis))))

(defn touchFile
  "Create an empty file"
  [filePath]
  (with-open [writer (io/writer filePath)]
    (.write writer "")))

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
  (is (true?
    (artifactPredicate "feature-" "#100" (io/file "feature-xxx-#100")))))

(deftest artifactPredicate-unmatch-prefix-test
  (is (false?
    (artifactPredicate "develop" "#100" (io/file "feature-xxx-#100")))))

(deftest artifactPredicate-unmatch-suffix-test
  (is (false?
    (artifactPredicate "feature-" "#101" (io/file "feature-xxx-#100")))))

(deftest findArtifact-test
  (let [artifacts [(io/file "feature-xxx-#200")
                  (io/file "feature-xxx-#101")
                  (io/file "feature-xxx-#100")]
       result (findArtifact artifacts "feature-" "-#100")]
       (is (= 1 (count result)))
       (is (= "feature-xxx-#100" (.getName (first result))))))

(deftest copyToRootWarFilename-test
  (let [currentMillis (millis)
        dirName (tmpDir "dir-copyToRootWarFilename-test" currentMillis)
        sourceFilename (str dirName "sourceFile-xxx" currentMillis ".war")
       ;;sourceFile (str tmpDir "sourceFile-" currentMillis ".war")
       artifactFile (io/file sourceFilename)
       targetFile (str dirName "sourceFile-/ROOT.war")]
    (io/delete-file targetFile true)
    (.mkdir (io/file dirName))
    (touchFile sourceFilename)
    (.mkdir (io/file (str dirName "sourceFile-")))
    (copyToRootWarFilename artifactFile "sourceFile-")
    (is (true? (.exists (io/file targetFile))))))

(deftest listFiles-test
  (let [list ["feature-xxx-#100" "feature-xxx-#101" "feature-xxx-#200"]
        currentMillis (millis)
        dirName (tmpDir "dir-listFiles-test" currentMillis)]
    ;; create test files
    (.mkdir (io/file dirName))
    (doall(map #(touchFile (str dirName %)) list))
    (is (= "feature-xxx-#101"
      (.getName (first (listFiles "feature-" "#101" dirName)))))
    (is (= "feature-xxx-#100"
      (.getName (first (listFiles "feature-" "#100" dirName)))))
    (is (= "feature-xxx-#200"
      (.getName (first (listFiles "feature-" "#200" dirName)))))))

(deftest prepareDir-test
  (let [currentMillis (millis)
        dirName (tmpDir "dir-prepareDir-test" currentMillis)
        dirPath (io/file dirName)
        filename (str dirName "/" "ROOT.war")
        file (io/file filename)]
    (is (false? (.exists dirPath)))
    (prepareDir (io/file filename) "feature-")
    (is (true? (.exists (io/file dirName))))
    (is (true? (.isDirectory dirPath)))
    (is (false? (.exists file)))
    ;; create a mocked ROOT.war file on directory
    (touchFile filename)
    ;;(with-open [writer (io/writer filename)]
    ;;  (.write writer ""))
    (prepareDir (io/file filename) "feature-")
    (is (true? (.exists (io/file dirName))))
    (is (true? (.isDirectory dirPath)))
    (is (true? (.isDirectory (io/file (str dirName "/feature-")))))
    (is (false? (.exists file)))))

(deftest releaseArtifact-test
  (let [currentMillis (millis)
    dirName (tmpDir "dir-releaseArtifact-test" currentMillis )
    dir (io/file dirName)
    featureDirName (str dirName "feature-")
    featureDir (io/file featureDirName)
    root (str dirName "feature-/ROOT.war")
    rootFile (io/file root)
    filenames (vector (str "feature-" currentMillis "-#100")
       (str "feature-" currentMillis "-#101")
       (str "feature-" currentMillis "-#200")
       (str "develop-" currentMillis "-#101"))]
       (.mkdir dir)
    (doseq [e filenames]
      (touchFile (str dirName e)))
    (releaseArtifact "feature-" "#101" dirName)
    (is (true? (.isDirectory featureDir)))
    (is (true? (and (.exists rootFile))))))

;;(deftest a-test
;;  (testing "FIXME, I fail."
;;    (is (= 0 1))))
