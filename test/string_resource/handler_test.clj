(ns string-resource.handler-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [string-resource.handler :refer :all]
            [cheshire.core :as json]))

(defmacro let-with-seed [string-store-seed & body]
  `(binding [*string-store* (->MockStringResourceStore (atom ~string-store-seed))]
      (let ~@body)))

(fact "gets to /string get string-resources"
  (fact "when a resource does not exist the service returns 404"
    (let-with-seed {} [response (app (mock/request :get "/string/not-real/en-US"))]
      (:status response) => 404
      (:body response) => "String resource not available"))
  (fact "when a resource does exist it is returned with a 200"
    (let-with-seed {["the-id" "language"] "You found me"}
      [response (app (mock/request :get "/string/the-id/language"))]
      (:status response) => 200
      (:body response) => "You found me")))

(defn mock-put [key language value]
    (->
      (mock/request :put (str "/string/" key "/" language)
        (json/generate-string {"value" value}))
      (mock/content-type "application/json")))

(fact "puts to /string change string resources"
  (fact "when a resource does not exist the resource is created"
    (let-with-seed {}
      [put-response (app (mock-put "new-key" "en-US" "This is the resource string"))
       get-response (app (mock/request :get "/string/new-key/en-US"))]
        (:status put-response) => 200
        (:body put-response) => true
        (:status get-response) => 200
        (:body get-response) => "This is the resource string"))
  (fact "when a resource does exist the resource is updated to the new value"
    (let-with-seed {["old-key" "en-US"] "This is the old string"}
      [put-response (app (mock-put "old-key" "en-US" "This is the resource string"))
       get-response (app (mock/request :get "/string/old-key/en-US"))]
        (:status put-response) => 200
        (:body put-response) => true
        (:status get-response) => 200
        (:body get-response) => "This is the resource string")))
