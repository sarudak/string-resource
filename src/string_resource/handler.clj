(ns string-resource.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defprotocol AStringResourceStore
  "An interface for storing string resources by langauge"
  (store [key language] "Stores a string resource")
  (retrieve [keys languages]
    "Retrieves all string resources associated with the given keys and languages"))

(def ^:dynamic string-store nil)

(defn get-string [key language] (retrieve string-store [key] [language]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/string/:key/:language" [key language] )
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
