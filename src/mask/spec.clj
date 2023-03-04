(ns mask.spec
  (:require
   [mask.core :as mask]
   [clojure.spec.alpha :as s]))


(s/def ::mask mask/mask?)
