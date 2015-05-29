(ns reagent-bosclj.pages
  "pages built with component composition for application"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent-bosclj.utils :as utils]))

(defn dashboard
  []
  [:div
   [:h2 "Dashboard"]])

(defn task
  []
  [:div
   [:h2 "Task Management"]])

(defn about
  []
  [:div
   [:h2 "About"]])
