(ns mask.core
  (:import
   java.io.Writer))


(defprotocol IMask
  (mask [this])
  (unmask [this]))


(deftype Mask [value]

  Object

  (equals [_ _]
    false)

  (toString [_this]
    "<< masked >>")

  IMask

  (mask [this]
    this)

  (unmask [_this]
    value))


(defn mask? [object]
  (instance? Mask object))


(extend-protocol IMask

  nil
  (mask [_]
    (throw
     (new IllegalArgumentException
          "Cannot mask a nil value")))

  (unmask [this]
    this)

  Object

  (mask [this]
    (new Mask this))

  (unmask [this]
    this))


(defmethod print-method Mask
  [masked ^Writer writer]
  (.write writer (str masked)))


(defn reader-tag [form]
  `(mask ~form))


(defn reader-edn [value]
  (mask value))
