(ns tabula-rasa.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as status]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(defonce people (atom []))

(defn add-person! [first-name last-name]
  (swap! people conj {:first-name (str/capitalize first-name)
                      :last-name  (str/capitalize last-name)}))

(add-person! "luiz" "strobelte")
(add-person! "Gisela" "Galati")

(defn simple-body-page [_]
  {:status  status/ok
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

(defn request-example [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "Request: " req)})

(defn hello-name [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "Hello " (get-in req [:params :name] "World"))})

(defn people-handler [_]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (str (json/write-str @people))})

(defn get-parameter [req & param-path] (get-in req (concat [:params] param-path)))
(defn add-person-handler! [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (-> (let [p (partial get-parameter req)]
                  (str (json/write-str (add-person! (p :first-name) (p :last-name))))))})

(defroutes app-routes
           (GET "/" [] simple-body-page)
           (GET "/request" [] request-example)
           (GET "/hello" [] hello-name)
           (GET "/people" [] people-handler)
           (POST "/people/add" [] add-person-handler!)
           (route/not-found "This is not the page you were looking for"))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (wrap-defaults #'app-routes site-defaults)
                       {:port port})
    (println (str "Server started at http://127.0.0.1:" port "/"))))
