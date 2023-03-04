(ns mask.aero
  (:require
   [mask.core :as mask]
   [aero.core :as aero]))


(defmethod aero/reader 'mask
  [_opts _tag value]
  (mask/mask value))
