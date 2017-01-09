(ns art_gallery.views.layout
  (:require [hiccup.page :refer :all]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to my art gallery!"]
     (include-css "/css/style.css")]
    [:body body]))
