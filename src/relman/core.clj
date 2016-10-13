(ns relman.core
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
  (if (not (containsSuffix artifact suffix))
    false
    (containsPrefix artifact prefix))) 
    ;;(= prefix (subs artifact 0 (count prefix)))))
   ;;(and (= prefix (subs artifact 0 (count prefix))) (= suffix (subs artifact (getSuffixIndex artifact suffix) (count artifact))))))

(defn findArtifact
  "Find artifact by prefix and suffix"
  [artifacts prefix suffix]
  (into []
    (filter #(artifactPredicate prefix suffix %) artifacts)))

(defn copyToRootWarFilename
  "Copy artifact path to ROOT.war"
  [artifact]
  (clojure.java.io/copy (clojure.java.io/file artifact) (clojure.java.io/file (str ("/tmp/" rootWarFilename)))))

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
