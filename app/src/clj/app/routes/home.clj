(ns app.routes.home
  (:require [app.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :as r]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "helios.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn login-page []
  (layout/render
    "login.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/home" [] (home-page))
  (GET "/" [] (login-page))
   (POST "/login-act" [username password] (if (and (= "bestman" username) (= "lalala" password))
                                            (r/redirect "/home")
                                            (r/redirect "/")))
  (GET "/about" [] (about-page)))

