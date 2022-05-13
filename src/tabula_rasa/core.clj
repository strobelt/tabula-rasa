(ns tabula-rasa.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [middleware.content-negotiation :refer :all])
  (:gen-class))

(defn request-example [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str req)})

(defn hello-name [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "Hello " (get-in req [:params :name] "World"))})

(defn get-parameter [req & param-path] (get-in req (concat [:params] param-path)))

(defroutes app-routes
           (GET "/" [] request-example)
           (GET "/hello" [] hello-name)
           (route/not-found "This is not the page you were looking for"))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (-> #'app-routes
                           (wrap-routes negotiate-content)
                           (wrap-defaults api-defaults))
                       {:port port})
    (println (str "Server started at http://127.0.0.1:" port "/"))))
