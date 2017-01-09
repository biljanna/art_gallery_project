(ns art_gallery.routes.home
  (:require [compojure.core :refer :all]
            [art_gallery.views.layout :as layout]
            [art_gallery.models.db :as db]            
            [clojure.string :as str]
            [hiccup.form :refer :all]
            [hiccup.core :refer [h]]
            [hiccup.page :as page]            
            [ring.util.response :as ring]))

(defroutes home-routes
  (GET "/" 
       []
       (indexpage))
  (GET "/add" 
       [] 
       (insert_or_update))
  (GET "/add" 
       [title author description price error id] 
       (insert_or_update title author description price error id))
  (GET "/showAll" 
       [] 
       (show))
  (POST "/save" 
        [title author description price id] 
        (save-painting title author description price id))
  (GET "/delete/:id"
       [id] 
       (delete-painting id))
  (GET "/update/:id"
       [id] 
       (show-painting (db/find-painting id))))

(defn indexpage []
  (layout/common
    [:h2 {:class "register-title"}
     "Welcome to my art gallery!"]
    [:br]
    [:div {:class "register-switch"}
     [:a {:href "/add" :class "register-switch-label"} "Add new painting" ]
     [:a {:href "/showAll" :class "register-switch-label "} "Show paintings" ]]
    [:img {:src "http://www.fantasyarts.net/wp-content/uploads/2015/01/winter-sparkle-original-madart-painting-megan-duncanson.jpg" :class "image"}]
    ))

(defn show []
  (layout/common
    [:h1 {:class "register-title"} "Paintings:"]
    (show-paintings)
    [:a {:href "/add" :class "register-button"} "Add new"]
    [:a {:href "/" :class "register-button"} "Home"]))

(defn show-paintings []
  [:table {:border 1 :class "table"}
   [:thead
    [:tr 
     [:th "Id"]
     [:th "Title"]
     [:th "Author"]     
     [:th {:width 244} "Description"]
     [:th "Price"]
     [:th "Time"]
     [:th "Delete"]
     [:th "Update"]]]
   (into [:tbody]
         (for [painting (db/get-all-paintings)]
           [:tr 
            [:td (:id painting)]
            [:td (:title painting)]
            [:td (:author painting)]
            [:td (:description painting)]
            [:td (:price painting)]
            [:td (format-time (:time painting))]
            [:td [:a {:class "button edit" :href (str "/delete/" (h (:id painting)))} "X"]]
            [:td [:a {:class "button delete" :href (str "/update/" (h (:id painting)))} "edit"]]]))])

(defn insert_or_update [& [title author description price error id]]
  (layout/common
  [:h2 {:class "register-title"} (if (nil? id) "Add your painting:" "Edit painting:")]
  (form-to {:id "frm_insert" :class "register"}
    [:post "/save"]
           (if (not (nil? id))
             [:p "Id:"])
           (if (not (nil? id))
               (text-field {:readonly true :class "register-input"} "id" id))
           [:p "Title:"]
           (text-field {:class "register-input"} "title" title)
           [:p "Author:"]
           (text-field {:class "register-input"} "author" author)           
           [:p "Description:"]
           (text-area {:rows 5 :cols 30 :class "register-input"} "description" description)
           [:p "Price:" ]
           (text-field {:id "price" :class "register-input"} "price" price)
           [:br] [:br]
           (submit-button {:onclick " return javascript:validateInsertForm()" :class "register-button"} (if (nil? id)"Insert" "Update"))
           [:hr]
           [:p {:style "color:red;"} error])
    [:a {:href "/" :class "register-button"} "Home"]))

(defn save-painting [title author description price &[id]]
  (cond
    (empty? title)
    (insert_or_update  title author description price "Title empty" id)
    (empty? author)
    (insert_or_update  title author description price "Author empty" id)   
    (empty? description)
    (insert_or_update  title author description price "Descrpition empty" id)    
    (nil? (parse-number price))
    (insert_or_update  title author description price "Price not valid" id)
    (<= (parse-number price) 0)    
    (insert_or_update  title author description price "Price not valid" id)    
    :else
  (do
    (if (nil? id)
      (db/add-new-painting title author description price)
      (db/update-painting id title author description price))
  (ring/redirect "/showAll"))))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn delete-painting [id]
  (when-not (str/blank? id)
    (db/delete-painting id))
  (ring/redirect "/showAll"))

(defn show-painting [painting]
  (insert_or_update (:title painting)
                 (:author painting) 
                 (:description painting) 
                 (:price painting) 
                 nil
                 (:id painting)))



