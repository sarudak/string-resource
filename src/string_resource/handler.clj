(ns string-resource.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :as json]))

(defprotocol AStringResourceStore
  "An interface for storing string resources by langauge"
  (store [this key language value] "Stores a string resource")
  (retrieve [this keys languages]
    "Retrieves all string resources associated with the given keys and languages"))

(def ^:dynamic *string-store* nil)

(defn get-resource [key language]
  (let [string-resource (-> (retrieve *string-store* [key] [language]) vals first)]
    (if string-resource
      string-resource
      {:status 404
        :body "String resource not available"})))

(defn set-resource [key language value]
  (store *string-store* key language value)
  {:status 200
    :body true})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/string/:key/:language" [key language] (get-resource key language))
  (PUT "/string/:key/:language" [key language value] (set-resource key language value))
  (POST "/string/find-all" [keys languages]
    {:status 200
      :body (json/generate-string (retrieve *string-store* keys languages))})
  (route/not-found "Not Found"))

(def app
  (->
    (wrap-defaults app-routes api-defaults)
    wrap-json-params))
