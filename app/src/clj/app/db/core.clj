(ns app.db.core
  (:require
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [app.config :refer [env]]
    [mount.core :refer [defstate]])
  (use dk.ative.docjure.spreadsheet)
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))


(def mysql-db {:dbtype "mysql"
               :dbname "wedding"
               :user "root"
               :password ""})


(defn data [] (read-string (slurp "resources/docs/undangan-ghea.edn")))

(defn check-user [name password]
  (let [exist? (first (filter #(get % name) (data)))]
    (if exist?
      (if (= password (:password (first (vals exist?))))
        true
        false)
      false)))


(comment
  (defstate ^:dynamic *db*
           :start (conman/connect! {:jdbc-url (env :database-url)})
           :stop (conman/disconnect! *db*))

  (conman/bind-connection *db* "sql/queries.sql"))

(defn search [s sources]
  (.contains (clojure.string/lower-case sources) (clojure.string/lower-case s)))

(defn search-all [s data]
  (filter #(search s %) (flatten (map keys data))))


(comment

  (defn raw []
   (filter #(not (nil? (:name %))) (->> (load-workbook "resources/docs/Kawinan.xlsx")
                                        (select-sheet "Undangan teman ghea")
                                        (select-columns {:B :name, :G :bridesmaid}))))

  (defn passwordify [raw number]
    (-> (->>
          (clojure.string/lower-case (:name raw))
          (clojure.string/trim))
        (clojure.string/replace #" " "")
        (str number)))

  (defn process-data [raw number]
    (->>
      (assoc raw :password (passwordify raw number) :username (-> (->>
                                                                    (clojure.string/lower-case (:name raw))
                                                                    (clojure.string/trim))
                                                                  (clojure.string/replace #" " "")))
      (hash-map (:name raw)))))
