(ns middleware.content-negotiation
  (:require [clojure.string :as str]
            [ring.middleware.content-type :refer :all]
            [ring.util.response :as res]))

(def acceptable-formats ["application/json" "application/edn" "text/html"])

(defn negotiate-content [handler]
  (fn [req]
    (println "Hi from content negotiation")
    (let [accept-header (get-in req [:headers "accept"])
          request-formats (map str/trim (str/split (str accept-header) #","))
          handled-formats (filter (fn [format] (some #(= format %) request-formats)) acceptable-formats)
          preferred-format (first (concat handled-formats acceptable-formats))]
      (println "This request accepts" acceptable-formats)
      (println "And the API serves" preferred-format)
      (-> req
          handler
          (res/content-type preferred-format)))))
