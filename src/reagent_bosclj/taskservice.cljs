(ns reagent-bosclj.taskservice
  "task service and service functions"
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :as async]
            [reagent-bosclj.events :as ev]
            [reagent-bosclj.utils :as utils]))
(enable-console-print!)
(defonce taskque (atom []))

(defn get-min-index
  "returns min index in taskque for given state, -1 if state not found"
  [state]
  (println @taskque)
  (let [task-ndx (map-indexed
                  #(if (= state (:state %2))
                     %1
                     999999)
                  @taskque)]
    (apply min task-ndx)))

(defn find-all-tasks
  "returns all tasks in queue"
  []
  @taskque)

(defn new-task
  "adds the task to the taskque"
  [task]
  (let [new-task (merge {:id (utils/uuid) :state :new} task)]
    (swap! taskque conj new-task)
    (ev/post-event (ev/AppEvent. :service-task-update :service new-task))))

(defn schedule-task
  "schedules next task in que"
  []
  (let [idx (get-min-index :new)]
    (when (> idx -1)
      (swap! taskque update-in [idx :state] (constantly :scheduled))
      (ev/post-event (ev/AppEvent. :service-task-update :service (get @taskque idx))))))

(defn complete-task
  "completes next task in que"
  []
  (let [idx (get-min-index :scheduled)]
    (when (> idx -1)
      (swap! taskque update-in [idx :state] (constantly :completed))
      (ev/post-event (ev/AppEvent. :service-task-update :service (get @taskque idx))))))

(defn purge-task-que
  "empties all tasks in queue"
  []
  (println "prging taskque"))

(defn reset-task-queue
  "resets tasks in que with random tasks"
  []
  (println "reset taskquee with random tasks:"))

;;; go block event handlers for services
(defn go-new-tasks
  "event handler for new task events"
  []
  (let [chan-new-task (async/chan)]
    (async/sub (ev/get-event-que) :new-ui-task chan-new-task)
    (go-loop []
      (let [v (:event-data (async/<! chan-new-task))]
        (new-task v))
      (recur))))

(defn go-update-tasks
  "event handler for update task events"
  []
  (let [chan-new-task (async/chan)]
    (async/sub (ev/get-event-que) :update-ui-task chan-new-task)
    (go-loop []
      (let [v (:event-data (async/<! chan-new-task))]
        (condp = v
          :schedule (schedule-task)
          :complete (complete-task)))
      (recur))))

(defn initialize-task-service
  "initializes task service by starting all event handlers"
  []
  (go-new-tasks)
  (go-update-tasks))
