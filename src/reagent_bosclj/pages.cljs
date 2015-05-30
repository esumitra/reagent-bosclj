(ns reagent-bosclj.pages
  "pages built with component composition for application"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.comp-tasks :as ctask]
   [reagent-bosclj.utils :as utils]))

(defn page-header
  [title message]
  [:div.jumbotron
   [:h2 title]
   [:p message]])

(defn dashboard
  []
  [:div
   [page-header "Task Dashboard"]
   [:div.row
    [:div.col-sm-4
     [ctask/task-panel "New" :new []]]
    [:div.col-sm-4
     [ctask/task-panel "Pending" :scheduled []]]
    [:div.col-sm-4
     [ctask/task-panel "Completed" :completed []]]]])

(defn task
  []
  (let [task-list (atom [])]
    (fn []
      [:div
       [page-header "Task Management"]
       [:div.panel
        (if (empty? @task-list)
          [ctask/task {:name "No tasks in task queue"}]
          (doseq [t @task-list]
            ^{:key (:id t)} [ctask/task t]))]])))

(defn about
  []
  [page-header "Tazki Demo Application" "This application was developed by Ed Sumitra with React and Reagent."])
