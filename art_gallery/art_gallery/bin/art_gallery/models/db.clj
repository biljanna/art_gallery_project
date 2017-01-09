(ns art_gallery.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "db.sq3"})

(defn create-db[]
  (sql/with-connection
    db
    (sql/create-table
      :painting
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:title "TEXT"]
      [:author "TEXT"]      
      [:description "TEXT"]
      [:price "REAL"]      
      [:time "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"])))

(defn get-all-paintings []
  (sql/with-connection
    db
    (sql/with-query-results res
      ["SELECT * FROM painting ORDER BY id"]
      (doall res))))

(defn find-painting [id]
  (first
    (sql/with-connection
      db
      (sql/with-query-results res
        ["SELECT * FROM painting WHERE id= ?" id]
        (doall res)))))

(defn add-new-painting [title author description price]
  (sql/with-connection
    db
    (sql/insert-values
      :painting
      [:title :author :description :price :time]
      [title author description price (new java.util.Date)])))

(defn delete-painting [id]
  (sql/with-connection
    db
    (sql/delete-rows
      :painting
      ["id=?" id])))

(defn update-painting [id title author description price]
  (sql/with-connection
    db
    (sql/update-values
      :painting
      ["id=?" id]
      {:title title :author author :description description :price price})))


