(ns string-resource.mongo-integration-tests
  (:use midje.sweet)
  (:require [string-resource.mongo-store :refer :all]
            [monger.collection :as mc]
            [string-resource.storage-protocol :refer :all]
            [string-resource.test-helpers :refer :all]))


(fact "When using mongoDB" :integration
      (let [mongo-store (->MongoStringResourceStore)]
        (mc/remove db "string-resources")
        (fact "We can insert data"
              (store mongo-store key1 english "some random string")
              (store mongo-store key1 french "a french string")
              (store mongo-store key2 english "another random string")
              (store mongo-store key2 spanish "a spanish string")
              (store mongo-store key3 english "one more string"))
        (fact "we can read back the inserted data"
              (let [data (retrieve mongo-store [key1 key2] [english])]
                (count data) => 2
                (get data (str key1 " " english)) => "some random string"
                (get data (str key2 " " english)) => "another random string"))
        (mc/remove db "string-resources")))



