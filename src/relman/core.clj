(ns relman.core
  (:require [clojure.java.io :as io]
            [clojure.tools.nrepl.server :as nrepl-server]
            [cider.nrepl :refer (cider-nrepl-handler)])
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
  "Copy artifact path to <prefix>/ROOT.war"
  [artifactFile prefix]
  (let [targetPath (.getParent artifactFile)]
    (io/copy artifactFile
      (io/file (str targetPath "/" prefix) rootWarFilename))))

(defn listFiles
  "List files by matching prefix and suffix from a directory path"
  [prefix suffix dirPath]
  (let [files (.listFiles (io/file dirPath))]
    (findArtifact files prefix suffix)))

(defn prepareDir
  "Prepare directory structure by deleting and creating a directory if it does not exist and deleting ROOT.war file if exists under this directory and create prefix directory."
  [artifactFile prefix]
  (let [dirPath (.getParent artifactFile)
    dir (io/file dirPath)
    branchTypeDir (io/file (str dirPath "/" prefix))
    rootWarFile (io/file dirPath rootWarFilename)]
    (if-not (.isDirectory dir)
      (.mkdir dir))
    (if-not (.isDirectory branchTypeDir)
      (.mkdir branchTypeDir))
    ;;(if (and (.exists artifactFile) (not (.isDirectory artifactFile)))
    (if (and (.exists rootWarFile) (not (.isDirectory rootWarFile)))
      (io/delete-file rootWarFile))
    artifactFile
  ))

(defn releaseArtifact
  "Release artifact"
  ([prefix suffix] (releaseArtifact prefix suffix 
                                    (.getParent (io/file "."))))
  ([prefix suffix dirPath]
    (let [artifacts (listFiles prefix suffix dirPath)]
      (cond
        (<= (count artifacts) 0) :no-artifacts-found
        (> (count artifacts) 1) :multiple-artifacts-found
        :else
          (do (copyToRootWarFilename
                (prepareDir (first artifacts) prefix) prefix)
          (first artifacts))
        ))))


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
