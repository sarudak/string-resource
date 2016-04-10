(ns string-resource.storage-protocol)

(defprotocol AStringResourceStore
  "An interface for storing string resources by langauge"
  (store [this key language value] "Stores a string resource")
  (retrieve [this keys languages]
    "Retrieves all string resources associated with the given keys and languages"))
