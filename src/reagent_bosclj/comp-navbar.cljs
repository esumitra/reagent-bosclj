(ns reagent-bosclj.comp-navbar
  "reusable navbar component"
  (:require
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [reagent.session :as session]
   [reagent-bosclj.utils :as utils]))

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
        ;; TODO: use a doseq
        (doall
         (for [nav-item items]
                  [:li {:key (utils/uuid) :class (when (= (:page nav-item) (session/get :page)) "active")}
                   [:a {:on-click #(secretary/dispatch! (:url nav-item))} (:name nav-item)]]))]]]])  )
