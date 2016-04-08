(ns string-resource.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defprotocol AStringResourceStore
  "An interface for storing string resources by langauge"
  (store [this key language value] "Stores a string resource")
  (retrieve [this keys languages]
    "Retrieves all string resources associated with the given keys and languages"))

(defrecord MockStringResourceStore [state]
  AStringResourceStore
  (store [this key language value] (swap! state #(assoc % [key language] value)))
  (retrieve [this keys languages] (@(:state this) [(first keys) (first languages)])))

(def ^:dynamic *string-store* (MockStringResourceStore. (atom {["the-id" "language"] "You found me"})))

(defn get-string [key language]
  (let [string-resource (retrieve *string-store* [key] [language])]
    (if string-resource
      string-resource
      {:status 404
        :body "String resource not available"})))
;(defn get-string [key language] (retrieve string-store [key] [language]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/string/:key/:language" [key language] (get-string key language))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
