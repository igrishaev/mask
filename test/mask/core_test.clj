(ns mask.core-test
  (:require
   [aero.core :as aero]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.test :refer [is deftest]]
   mask.aero
   [mask.core :as mask]
   [mask.spec :as spec]))


(deftest test-mask-ok

  (is (= "<< masked >>"
         (str (mask/mask 42))))

  (is (= "{:aaa {:bbb << masked >>}}"
         (pr-str {:aaa {:bbb (mask/mask 42)}})))

  (is (not= (mask/mask 1)
            (mask/mask 1)))

  (is (= 42
         (-> 42
             (mask/mask)
             (mask/mask)
             (mask/mask)
             (mask/unmask)
             (mask/unmask)
             (mask/unmask))))

  (is (thrown? IllegalArgumentException
               (mask/mask nil)))

  (is (nil? (mask/unmask nil))))


(deftest test-reader-tag

  (is (= 42
         (-> {:foo #mask 42}
             (:foo)
             (mask/unmask)))))


(deftest test-aero-tag

  (let [source
        (-> "{:foo #mask 42}"
            (.getBytes)
            (io/input-stream))

        config
        (aero/read-config source)]

    (is (map? config))
    (is (= 42
           (-> config
               :foo
               mask/unmask)))))


(deftest test-edn-tag

  (let [source
        (-> "{:foo #mask 42}")

        config
        (edn/read-string {:readers {'mask mask/reader-edn}}
                         source)]

    (is (= 42
           (-> config
               :foo
               mask/unmask)))))


(s/def ::username string?)
(s/def ::password ::spec/mask)

(s/def ::config
  (s/keys :req-un [::username
                   ::password]))


(deftest test-spec

  (let [config
        {:username "Ivan"
         :password #mask "secret"}]

    (is (s/valid? ::config config)))

  (let [config
        {:username "Ivan"
         :password "not masked"}]

    (is (not (s/valid? ::config config)))))
