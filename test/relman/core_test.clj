(ns relman.core-test
  (:require [clojure.test :refer :all]
            [relman.core :refer :all]))

;;(deftest printUsage-test
;;  (printUsage))

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
  (is (true? (artifactPredicate "feature-" "#100" "feature-xxx-#100"))))

(deftest artifactPredicate-unmatch-prefix-test
  (is (false? (artifactPredicate "develop" "#100" "feature-xxx-#100"))))

(deftest artifactPredicate-unmatch-suffix-test
  (is (false? (artifactPredicate "feature-" "#101" "feature-xxx-#100"))))

(deftest findArtifact-test
  (let [artifacts ["feature-xxx-#200" "feature-xxx-#101" "feature-xxx-#100"]
       result (findArtifact artifacts "feature-" "-#100")]
  (is (= 1 (count result)))
  (is (= "feature-xxx-#100" (first result)))))

;;(deftest a-test
;;  (testing "FIXME, I fail."
;;    (is (= 0 1))))
