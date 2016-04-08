(ns string-resource.handler-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [string-resource.handler :refer :all]))

(fact "gets to /string get string-resources"
  (fact "when a resource does not exist the service returns 404"
    (let [response (app (mock/request :get "/string/not-real/en-US"))]
      (:status response) => 404
      (:body response) => "String resource not available"))
  (fact "when a resource does exist it is returned with a 200"
    (binding [*string-store* (->MockStringResourceStore
      (atom {["the-id" "language"] "You found me"}))]
      (let [response (app (mock/request :get "/string/the-id/language"))]
        (:status response) => 200
        (:body response) => "You found me"))))
