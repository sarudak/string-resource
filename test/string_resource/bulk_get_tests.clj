(ns string-resource.bulk-get-tests
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [string-resource.handler :refer [app]]
            [string-resource.test-helpers :refer :all]
            [cheshire.core :as json]))

(def seed-data {
  [key1 english] "This is something to say"
  [key2 english]  "Now saying something else"
  [key1 french] "Ceci est quelque chose à dire"
  [key2 french] "Maintenant dire autre chose"
  [key3 french] "Ceci est seulement en français"
  [key1 spanish] "Esto es algo que decir"})

(defn mock-post [keys languages]
  (-> (mock/request :post "/string/find-all"
        (json/generate-string {:keys keys :languages languages}))
      (mock/content-type "application/json")))

(fact "Posts to string/find-all gets the cartesian product of keys and languages"
  (fact "when querying for existent data all data is returned"
    (let-with-seed seed-data
      [response (app (mock-post [key1 key2] [english french]))
       parsed-body (json/parse-string (:body response))]
      (:status response) => 200
      (count parsed-body) => 4
      parsed-body => (contains [[(str key1 " " english) "This is something to say"]])
      parsed-body => (contains [[(str key2 " " english) "Now saying something else"]])
      parsed-body => (contains [[(str key1 " " french) "Ceci est quelque chose à dire"]])
      parsed-body => (contains [[(str key2 " " french) "Maintenant dire autre chose"]])))

  (fact "when querying for entries that do not exist existent entries are returned"
    (let-with-seed seed-data
      [response (app (mock-post [key1 key3] [english french spanish]))
       parsed-body (json/parse-string (:body response))]
      (:status response) => 200
      (count parsed-body) => 4
      parsed-body => (contains [[(str key1 " " english) "This is something to say"]])
      parsed-body => (contains [[(str key1 " " spanish) "Esto es algo que decir"]])
      parsed-body => (contains [[(str key1 " " french) "Ceci est quelque chose à dire"]])
      parsed-body => (contains [[(str key3 " " french) "Ceci est seulement en français"]]))))
