(ns app.routes.home
  (:require [app.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :as r]
            [clojure.java.io :as io]
            [noir.session :as session]
            [app.db.core :as db]))

(defn home-page []
  (layout/render
    "helios.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn home-page-with-search [s]
  (layout/render
    "helios.html" {:items (db/search-all s (db/data))}))

(defn login-page []
  (layout/render
    "login.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (login-page))
  (GET "/home" []
    (if (session/get :username)
                              (home-page)
                              (r/redirect "/")))
  (POST "/login-act" [username password] (if (db/check-user username password)
                                           (do
                                             (session/put! :username username)
                                             (r/redirect "/home"))
                                            (r/redirect "/")))
  (GET "/about" [] (about-page)))

