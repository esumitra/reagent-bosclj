(ns reagent-bosclj.events
  "event functions to setup up event que and helper functions to subscribe to and add to event queue"
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :as async]
            [reagent-bosclj.utils :as utils]))

(enable-console-print!)

(def ^:dynamic *event-que* nil)
(def ^:dynamic *chan-data* nil)

;; event record
;; event-type - type of event
;; :service-task-update - updated tasks with event-data = list of updated tasks
;; :ui-add-new-task - new task added through ui with event data = task data for newly added task
;; :ui-update-task - new task added through ui with event data = :accept to accept a task,:complete to complete a task
;; event-src - source of event
;; :server-poll - data retrieved from server poll
;; :ui - data from UI
(defrecord AppEvent [event-type event-src event-data])
(defn initialize-event-que
  "creates and returns event que (flux) for application"
  []
  (let [chan-data (async/chan)
        pub-data (async/pub chan-data :event-type)]
    (set! *event-que* pub-data)
    (set! *chan-data* chan-data)
    [chan-data pub-data]))

(defn get-event-que
  []
  *event-que*)

(defn post-event
  "posts an event to the event que"
  [e]
  (println "posting event:" e)
  (go (async/>! *chan-data* e)))

;; make this a generic topic logger instead of a specific topic logger
(defn go-logger
  "go block that logs all events in the que"
  []
  (let [chan-log (async/chan)]
    (async/sub *event-que* :new-ui-task chan-log)
    (async/sub *event-que* :update-ui-task chan-log)
    (go-loop []
      (let [v (async/<! chan-log)]
        (println "new event: " v))
      (recur))))
