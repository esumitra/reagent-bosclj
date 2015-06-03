(ns reagent-bosclj.pages
  "pages built with component composition for application"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.comp-tasks :as ctask]
   [reagent-bosclj.taskservice :as ts]
   [reagent-bosclj.utils :as utils]))

(defn page-header
  [title message]
  [:div.jumbotron
   [:h2 title]
   [:p message]])

(defn dashboard
  []
  (let [task-map (group-by :state (ts/find-all-tasks))]
    [:div
     [page-header "Task Dashboard"]
     [:div.well
      [ctask/new-task]]
     [:div.row
      [:div.col-sm-4
       [ctask/task-panel "New" :new (vec (:new task-map))]]
      [:div.col-sm-4
       [ctask/task-panel "Pending" :scheduled (vec (:scheduled task-map))]]
      [:div.col-sm-4
       [ctask/task-panel "Completed" :completed (vec (:completed task-map))]]]]))

(defn task
  []
  (let [task-list (atom [])]
    (fn []
      [:div
       [page-header "Task Management"]
       [:div.panel
        (if (empty? @task-list)
          [ctask/task {:name "No tasks in task queue"}]
          (for [t @task-list]
            ^{:key (:id t)} [ctask/task t]))]])))

(defn about
  []
  [page-header "Tazki Demo Application" "This application was developed by Ed Sumitra with React and Reagent."])
