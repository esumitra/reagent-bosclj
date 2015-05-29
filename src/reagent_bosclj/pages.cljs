(ns reagent-bosclj.pages
  "pages built with component composition for application"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.comp-tasks :as ctask]
   [reagent-bosclj.utils :as utils]))

(defn dashboard
  []
  [:div
   [:h2 "Task Dashboard"]
   [:div.panel [:h1 "Add tasks"]]
   [:div.panel
    [:div.row
     [:div.col-sm-4
      [ctask/task-panel "New" :new []]]
     [:div.col-sm-4
      [ctask/task-panel "Pending" :scheduled []]]
     [:div.col-sm-4
      [ctask/task-panel "Completed" :completed []]]]]])

(defn task
  []
  (let [task-list (atom [])]
    (fn []
      [:div
       [:h2 "Task Management"]
       [:div.panel
        (if (empty? @task-list)
          [ctask/task {:name "No tasks in task queue"}]
          (doseq [t @task-list]
            ^{:key (:id t)} [ctask/task t]))]])))

(defn about
  []
  [:div.jumbotron
   [:h2 "Tazki Demo Application"]
   [:p "This application was developed by Ed Sumitra with React and Reagent."]])
