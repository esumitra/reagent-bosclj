(ns reagent-bosclj.comp-tasks
  "reusable task and task panel components"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.utils :as utils]))

(defn task
  "displays a task"
  [& {:keys [name desc ui-state]} args]
  (let [class (condp = ui-state
                :new "well animated shake"
                "well")]
    [:div {:class class} name]))

(defn task-panel
  "displays tasks in a panel"
  [name state init-task-list]
  (let [cname (condp = state
                :new "panel-info"
                :scheduled "panel-warning"
                :completed "panel-success")
        task-list (atom init-task-list)]
    (fn [] 
      [:div {:class (str "panel " cname)}
       [:div.panel-heading 
        [:h3.panel-title name]]
       [:div.panel-body
        (if (empty? @task-list)
          [task {:name "No tasks in this state"}]
          (for [t @task-list]
            ^{:key (:id t)} [task t]))]])))
