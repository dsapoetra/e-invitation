(ns app.db.core
  (:require
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [app.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))


(def mysql-db {:dbtype "mysql"
               :dbname "wedding"
               :user "root"
               :password ""})


(comment
  (defstate ^:dynamic *db*
           :start (conman/connect! {:jdbc-url (env :database-url)})
           :stop (conman/disconnect! *db*))

  (conman/bind-connection *db* "sql/queries.sql"))


