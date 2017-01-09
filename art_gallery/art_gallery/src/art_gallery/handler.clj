(ns art_gallery.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [art_gallery.routes.home :refer [home-routes]]
            [art_gallery.models.db :as db]))

(defn init []
  (if-not (.exists (java.io.File. "./db.sq3"))
    (db/create-db)))

(defn destroy []
  (println "goodbye!"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Page Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
