(ns ^:figwheel-always reagent-bosclj.core
    (:require
     [secretary.core :as secretary]
     [reagent.session :as session]
     [reagent.core :as reagent :refer [atom]]
     [reagent-bosclj.utils :as utils]
     [reagent-bosclj.components :as rcomp]
     [reagent-bosclj.pages :as pages]
     [reagent-bosclj.events :as ev]
     [reagent-bosclj.taskservice :as ts])
    (:require-macros [secretary.core :refer [defroute]]))

(enable-console-print!)

(defonce navbar-data
  {:brand "Tazki" 
   :items [
           {:name "Dashboard" :page :home :url "#/"}
           {:name "Task Que" :page :task :url "#/task"}
           {:name "About" :page :about :url "#/about"}]})

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
  (ev/initialize-event-que)
  (ev/go-logger)
  (ts/initialize-task-service)
  (utils/mount-component rcomp/navbar navbar-data "navbar")
  (utils/mount-component page nil "app"))

(defn on-js-reload
  "figwheel reload initialization goes here"
  [])

(init!)
