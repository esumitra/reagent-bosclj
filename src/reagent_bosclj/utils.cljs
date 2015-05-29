(ns reagent-bosclj.utils
  "clojurescript utils"
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.string :as string]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(defn uuid
  "returns a type 4 random UUID: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"
  []
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))


(defn ajax-get
  "returns a channel with the result of the json request to uri with a map of pars parameters
  channel entries are of the form {:success true/false :response response/error-text}"
  [url pars]
  (let [out (async/chan)]
    (GET url
         {:headers {"Content-Type" "text/plain"}
          :params pars
          :handler #(async/put! out {:success true :response %})
          :error-handler #(async/put! out {:success false :response %2})})
    out))

(defn mount-component
  "mounts the component cmp with properties props at dom-id
  e.g., (mount-component maze1.logger/logger-coponent myprop \"logger\")"
  [cmp props dom-id]
  (reagent/render
    [cmp props]
    (.getElementById js/document dom-id)))
