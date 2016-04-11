(ns string-resource.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [string-resource.storage-protocol :refer :all]
            [string-resource.mongo-store :refer [build-mongo-store]]
            [cheshire.core :as json]
            [ring.logger :as logger]))

(def ^:dynamic *string-store*)

(defn get-resource [key language]
  (let [string-resource (-> (retrieve *string-store* [key] [language]) vals first)]
    (if string-resource
      string-resource
      {:status 404
        :body "String resource not available"})))

(defn set-resource [key language value]
  (store *string-store* key language value)
  {:status 200
    :body "true"})

(defroutes app-routes
  (GET "/" [] "Service operational")
  (GET "/string/:key/:language" [key language] (get-resource key language))
  (PUT "/string/:key/:language" [key language value] (set-resource key language value))
  (POST "/string/find-all" [keys languages]
    {:status 200
      :body (json/generate-string (retrieve *string-store* keys languages))})
  (route/not-found "Not Found"))

(defn use-mongodb-store [handler]
  (fn [request]
    (binding [*string-store* (build-mongo-store {})]
      (handler request))))

(def app
  (->
    (wrap-defaults app-routes api-defaults)
    wrap-json-params))

(def live-app
  (-> app
    logger/wrap-with-logger
    use-mongodb-store))
