(ns relman.core
  (:require [clojure.java.io :as io])
  (:gen-class))

(def rootWarFilename
  "Get ROOT.war filename"
  "ROOT.war")

(defn getSuffixIndex
  "Get suffix index position, returns -1 if no match"
  [text suffix]
  (.lastIndexOf text suffix))

(defn containsSuffix
  "Contains suffix"
  [text suffix]
  (> (getSuffixIndex text suffix) -1))

(defn containsPrefix
  "Contains prefix"
  [text prefix]
  ;; NOTE: could have used String indexOf method
  (= prefix (subs text 0 (count prefix))))


(defn artifactPredicate
  "An artifact predicate indicating if file contains prefix and suffix"
  [prefix suffix artifact]
  (if (not (containsSuffix (.getName artifact) suffix))
    false
    (containsPrefix (.getName artifact) prefix))) 

(defn findArtifact
  "Find artifact as file by prefix and suffix"
  [artifacts prefix suffix]
  (into []
    (filter #(artifactPredicate prefix suffix %) artifacts)))

(defn copyToRootWarFilename
  "Copy artifact path to ROOT.war"
  [artifactFile]
  (let [targetPath (.getParent artifactFile)]
    (io/copy artifactFile
      (io/file targetPath  rootWarFilename))))

(defn listFiles
  "List files by matching prefix and suffix"
  [prefix suffix dirPath]
  (let [files (.listFiles (io/file dirPath))]
    (findArtifact files prefix suffix)))

(defn releaseArtifact
  "Release artifact"
  [filename]
  (println "Release artifact"))

(defn printUsage
  "Print usage"
  []
  (println "Usage: relman <war-file>"))

(defn relman
  "RelMan function"
  [& args]
  ())

(defn -main
  "RelMan CLI"
  [& args]
  ())
