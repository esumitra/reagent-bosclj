(ns reagent-bosclj.taskservice
  "task service and service functions"
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :as async]
            [reagent-bosclj.events :as ev]
            [reagent-bosclj.utils :as utils]))
(enable-console-print!)
(defonce taskque (atom []))

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

(defn accept-task
  "accepts next task in que"
  []
  (println "task accepted:"))

(defn complete-task
  "completes next task in que"
  []
  (println "task completed:"))

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

(defn initialize-task-service
  "initializes task service by starting all event handlers"
  []
  (go-new-tasks))
