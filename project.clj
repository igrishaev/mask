(defproject com.github.igrishaev/mask "0.1.2-SNAPSHOT"

  :description
  "A small library to prevent secrets from being logged or printed."

  :deploy-repositories
  {"releases" {:url "https://repo.clojars.org" :creds :gpg}}

  :url
  "https://github.com/igrishaev/mask"

  :license
  {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url "https://www.eclipse.org/legal/epl-2.0/"}

  :release-tasks
  [["vcs" "assert-committed"]
   ["test"]
   ["change" "version" "leiningen.release/bump-version" "release"]
   ["vcs" "commit"]
   ["vcs" "tag" "--no-sign"]
   ["deploy"]
   ["change" "version" "leiningen.release/bump-version"]
   ["vcs" "commit"]
   ["vcs" "push"]]

  :dependencies
  []

  :profiles
  {:dev
   {:main mask.core
    :dependencies
    [[org.clojure/clojure "1.11.1"]
     [aero "1.1.6"]]}})
