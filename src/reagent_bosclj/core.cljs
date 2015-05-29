(ns ^:figwheel-always reagent-bosclj.core
    (:require
     [secretary.core :as secretary]
     [reagent.session :as session]
     [reagent.core :as reagent :refer [atom]]
     [reagent-bosclj.utils :as utils]
     [reagent-bosclj.comp-navbar :as cnavbar]
     [reagent-bosclj.pages :as pages])
    (:require-macros [secretary.core :refer [defroute]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console. Really!")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))
(defonce appdata
  {:navbar
   {:brand "Tazki" 
    :items [
            {:name "Dashboard" :page :home :url "#/"}
            {:name "Task Que" :page :task :url "#/task"}
            {:name "About" :page :about :url "#/about"}]}})

;; setup secretary
(def pages
  {:home pages/dashboard
   :task pages/task
   :about pages/about})

(defn page []
  [(pages (session/get :page))])

(defroute "/" [] (session/put! :page :home))
(defroute "/task" [] (session/put! :page :task))
(defroute "/about" [] (session/put! :page :about))

;; initialize
(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :page :home)
  (utils/mount-component cnavbar/navbar (:navbar appdata) "navbar")
  (utils/mount-component page nil "app")
  (println "initializer called"))

(init!)
