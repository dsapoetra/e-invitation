(ns app.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [app.layout :refer [error-page]]
            [app.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [app.env :refer [defaults]]
            [mount.core :as mount]
            [ring.middleware.session.memory :refer [memory-store]]
            [noir.session :as session]
            [app.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(def app
  (session/wrap-noir-session*
    (middleware/wrap-base #'app-routes)))

