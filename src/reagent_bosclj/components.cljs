;;;; reusable components
;;;; page-header - jumbotron panel for page header
;;;; navbar - navbar components that displays a brand and a list of menu items
;;;; task - displays a task name in a well
;;;; task panel - self updating panel that displays tasks in an input state
;;;; task form - form to enter new tasks and act on tasks
(ns reagent-bosclj.components
  "reusable components for demo"
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljs.core.async :as async]
   [secretary.core :as secretary]
   [reagent.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.events :as ev]
   [reagent-bosclj.utils :as utils]))

(defn page-header
  [title message]
  [:div.jumbotron
   [:h2 title]
   [:p message]])


(defn navbar
  "generates a navbar given a map with :brand :items list with each item having a :label and :url"
  [props]
  ;; use parameter destructuring
  (let [brand (:brand props)
        items (:items props)]
    [:div.navbar.navbar-inverse.navbar-default
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href "#/"} brand]]
      [:div.navbar-collapse.collapse
       [:ul.nav.navbar-nav
        (doall
         (for [nav-item items]
                  [:li {:key (utils/uuid) :class (when (= (:page nav-item) (session/get :page)) "active")}
                   [:a {:on-click #(secretary/dispatch! (:url nav-item))} (:name nav-item)]]))]]]])  )

(defn task
  "displays a task"
  [& {:keys [name desc ui-state]} props]
  (let [class (condp = ui-state
                :new "well animated shake"
                "well")]
    [:div {:class class}
      name]))

(defn go-task-update
  "subscribes to state channel and updates ref-list with new items and deletes old items"
  [state ref-list]
  (let [chan-data (async/chan)]
    (async/sub (ev/get-event-que) :service-task-update chan-data)
    (go-loop []
      (let [new-task (:event-data (async/<! chan-data))
            next-task (first @ref-list)]
        (cond
          (= state (:state new-task)) (swap! ref-list conj new-task)
          (and (not (nil? next-task))
               (= (:id new-task) (:id next-task))) (reset! ref-list (subvec @ref-list 1))))
      (recur))))

;; span.badge number
(defn task-panel
  "displays tasks in a panel"
  [name state init-task-list]
  (let [cname (condp = state
                :new "panel-info"
                :scheduled "panel-warning"
                :completed "panel-success")
        task-list (atom init-task-list)]
    (go-task-update state task-list)
    (fn [] 
      [:div {:class (str "panel " cname)}
       [:div.panel-heading 
        [:h3.panel-title name]]
       [:div.panel-body
        (if (empty? @task-list)
          [task {:name "No tasks in this state"}]
          (for [t @task-list]
            ^{:key (:id t)} [task t]))]])))

(defn new-task
  "new task form"
  []
  (let [task-data (atom {:name nil :desc nil})] ;; local variable for new task form
    (fn []
      [:form.form-vertical
       [:fieldset
        [:span.col-sm-2.form-group
         [:span
          [:input#taskName.form-control
           {:placeholder "Task Name"
            :value (:name @task-data)
            :on-change #(swap! task-data assoc-in [:name] (.-target.value %))}]]]
        [:span.col-sm-2.form-group
         [:span
          [:input#taskDesc.form-control
           {:placeholder "Task Description"
            :value (:desc @task-data)
            :on-change #(swap! task-data assoc-in [:desc] (.-target.value %))}]]]
        [:span.col-sm-2
         [:a.btn.btn-info.btn-raised
          {:on-click
           (fn [e]
             (when-let [_ (:name @task-data)]
               (ev/post-event (ev/AppEvent. :new-ui-task :ui @task-data))
               (reset! task-data {:name nil :desc nil})))} "Add Task"]]
        [:span.col-sm-2
         [:a.btn.btn-warning.btn-raised
          {:on-click
           (fn [e]
             (ev/post-event (ev/AppEvent. :update-ui-task :ui :schedule)))} "Schedule Task"]]
        [:span.col-sm-2
         [:a.btn.btn-success.btn-raised
          {:on-click
           (fn [e]
             (ev/post-event (ev/AppEvent. :update-ui-task :ui :complete)))} "Complete Task"]]]])))

