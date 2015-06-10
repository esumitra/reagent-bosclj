(ns reagent-bosclj.pages
  "pages built with component composition for application"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.components :as rcomp]
   [reagent-bosclj.taskservice :as ts]
   [reagent-bosclj.utils :as utils]))

(defn dashboard
  []
  (let [task-map (group-by :state (ts/find-all-tasks))]
    [:div
     [rcomp/page-header "Task Dashboard"]
     [:div.well
      [rcomp/new-task]]
     [:div.row
      [:div.col-sm-4
       [rcomp/task-panel "New" :new (vec (:new task-map))]]
      [:div.col-sm-4
       [rcomp/task-panel "Pending" :scheduled (vec (:scheduled task-map))]]
      [:div.col-sm-4
       [rcomp/task-panel "Completed" :completed (vec (:completed task-map))]]]]))

(defn task
  []
  (let [task-list (ts/find-all-tasks)#_[]]
    (fn []
      [:div
       [rcomp/page-header "Task Management"]
       [:div.panel
        (if (empty? task-list)
          [rcomp/task {:name "No tasks in task queue"}]
          (for [t task-list]
            ^{:key (:id t)} [rcomp/task t]))]])))

(defn about
  []
  [rcomp/page-header "Tazki Demo Application" "This application was developed by Ed Sumitra with React and Reagent."])
