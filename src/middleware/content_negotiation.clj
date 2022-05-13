(ns middleware.content-negotiation
  (:require [clojure.string :as str]
            [ring.middleware.content-type :refer :all]))

(def acceptable-formats ["application/json" "application/edn" "text/html"])

(defn negotiate-content [handler]
  (fn [req]
    (println "Hi from content negotiation")
    (let [accept-header (get-in req [:headers "accept"])
          request-formats (str/split accept-header #",")
          preferred-format (filter (fn [format] (some #(= format %) acceptable-formats)) request-formats)]
      (println "This request accepts" acceptable-formats)
      (println "And the API handles" preferred-format)
      (handler req))))
