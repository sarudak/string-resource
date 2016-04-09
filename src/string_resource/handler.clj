(ns string-resource.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(defprotocol AStringResourceStore
  "An interface for storing string resources by langauge"
  (store [this key language value] "Stores a string resource")
  (retrieve [this keys languages]
    "Retrieves all string resources associated with the given keys and languages"))

(defrecord MockStringResourceStore [state]
  AStringResourceStore
  (store [this key language value] (swap! state #(assoc % [key language] value)))
  (retrieve [this keys languages] (@(:state this) [(first keys) (first languages)])))

(def ^:dynamic *string-store* nil)

(defn get-resource [key language]
  (let [string-resource (retrieve *string-store* [key] [language])]
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
  (route/not-found "Not Found"))

(def app
  (->
    (wrap-defaults app-routes api-defaults)
    wrap-json-params))
