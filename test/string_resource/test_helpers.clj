(ns string-resource.test-helpers
  (:require [string-resource.handler :refer [*string-store*]]
            [string-resource.storage-protocol :refer [AStringResourceStore]]))

(def key1 "somethingtosay")
(def key2 "somethingelse")
(def key3 "anotherkey")
(def english "en-us")
(def french "fr-fr")
(def spanish "es-419")

(defn get-all [data keys languages]
    (->>
      (for [key keys
            lang languages]
            [(str key " " lang) (data [key lang])])
      (filter #(identity (second %)))
      (into {})))

(defrecord MockStringResourceStore [state]
  AStringResourceStore
  (store [this key language value] (swap! state #(assoc % [key language] value)))
  (retrieve [this keys languages] (get-all @state keys languages)))

(defmacro let-with-seed [string-store-seed & body]
  `(binding [*string-store* (->MockStringResourceStore (atom ~string-store-seed))]
      (let ~@body)))
