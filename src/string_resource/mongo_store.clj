(ns string-resource.mongo-store
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [string-resource.storage-protocol :refer [AStringResourceStore]])
  (:import [com.mongodb MongoOptions ServerAddress]))


;(def db (mg/get-db (mg/connect) "local"))

(defn store-in-db [conn key language value]
  (mc/update conn "string-resources" {:_id (str key " " language)} {:key key :language language :value value} {:upsert true}))

(defn get-all-from-db [conn keys languages]
  (->> (mc/find-maps conn "string-resources" {:key {"$in" keys} :language {"$in" languages}})
       (map (fn [{:keys [_id value]}] [_id value]))
       (into {})))

(defn build-mongo-store [connection-options]
  (let [connection (mg/get-db (mg/connect connection-options) "local")]
    (reify AStringResourceStore
      (store [_ key language value] (store-in-db connection key language value))
      (retrieve [_ keys languages] (get-all-from-db connection keys languages)))))
