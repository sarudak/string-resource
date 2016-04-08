(ns string-resource.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [string-resource.handler :refer :all]))

(deftest test-get
  (testing "Nonexistent resource"
    (let [response (app (mock/request :get "/string/not-real/en-US"))]
      (is (= (:status response) 404))
      (is (= (:body response) "String resource not available"))))
  (testing "Existing resource is returned"
    (binding [*string-store* (->MockStringResourceStore
      (atom {["the-id" "language"] "You found me"}))]
      (let [response (app (mock/request :get "/string/the-id/language"))]
        (is (= (:status response) 200))
        (is (= (:body response) "You found me"))))))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
