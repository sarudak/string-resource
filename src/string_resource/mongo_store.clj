(ns string-resource.mongo-store
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [string-resource.storage-protocol :refer [AStringResourceStore]])
  (:import [com.mongodb MongoOptions ServerAddress]))


(def db (mg/get-db (mg/connect) "local"))

(defn store-in-db [key language value]
  (mc/update db "string-resources" {:_id (str key " " language)} {:key key :language language :value value} {:upsert true}))

(defn get-all-from-db [keys languages]
  (->> (mc/find-maps db "string-resources" {:key {"$in" keys} :language {"$in" languages}})
       (map (fn [{:keys [_id value]}] [_id value]))
       (into {})))

(defrecord MongoStringResourceStore []
  AStringResourceStore
  (store [_ key language value] (store-in-db key language value))
  (retrieve [_ keys languages] (get-all-from-db keys languages)))



;(let [conn (mg/connect)
;      db   (mg/get-db conn "local")]
;    (mc/update db "documents" {:_id "id3"} {:color "Red" :shape "Circle"} {:upsert true}))


;(let [conn (mg/connect)
;      db   (mg/get-db conn "local")]
;    (mc/find-maps db "documents" {:name {"$in" names}}))


