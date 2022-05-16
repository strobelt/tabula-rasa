(ns middleware.content-negotiation
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]
            [ring.middleware.content-type :refer :all]
            [ring.util.response :as res]))

(def acceptable-formats {"application/json" json/write-str
                         "application/edn"  constantly})

(defn get-formatter [handled-formats formats]
  (-> handled-formats
      (select-keys formats)
      (conj (first handled-formats))
      first))
(def ^:private format-gen #(g/map (g/elements ["application/json" "application/edn"])
                                  (g/elements [str int map])
                                  {:min-elements 1}))
(s/def ::format-handler (s/with-gen (s/map-of string? fn?) format-gen))
(g/sample (s/gen ::format-handler))
(s/fdef get-formatter
        :args (s/cat :handled-formats ::format-handler :formats (s/coll-of string?))
        :ret map-entry?
        :fn #(let [ret (->> % :ret (apply hash-map))
                   handled-formats (-> % :args :handled-formats)]
               (= (select-keys handled-formats (keys ret)) ret)))

(defn negotiate-content [handler]
  (fn [req]
    (let [request-formats (-> (get-in req [:headers "accept"])
                              str
                              (str/split #","))
          preferred-format (get-formatter acceptable-formats request-formats)]
      (println "This request accepts" acceptable-formats)
      (println "And the API serves" preferred-format)
      (-> req
          handler
          (res/content-type preferred-format)))))
