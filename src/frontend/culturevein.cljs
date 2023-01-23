(ns ^:figwheel-hooks frontend.culturevein
  (:require
  [goog.dom :as gdom]
  [reagent.core :as r :refer [atom]]
  [reagent.dom :as rdom]
  [ajax.core :as ajax]
  (clojure.string :as str)))

(def value (r/atom ""))
(defonce app-state (atom {:title "CultureVein"
                          :tags {}}))

(defn handle-response [resp]
  (swap! app-state assoc :tags (get-in resp ["data"])))

(defn get-tags []
  (ajax/GET "http://localhost:8000/api/tags"
            {:handler handle-response}))

(get-tags)

(defn title []
  [:h1 (:title @app-state)])

(defn shared-state []
  [:div
   [:p "The value is now: " @value]])

(defn main-search []
  [:input {:type "text"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}])

(defn list-tags [items]
  (let [results (for [item items
                      :when (str/includes? (get-in item ["name_lowercase"]) @value)] item)]
    [:div
     [:p "counter: " (count results)]
     [:ul (doall (for [result results]
                   [:li {:key (get-in result ["tag_slug_id"])} (get-in result ["tag_name"])]))]]))

(defn app []
  [:div.app
   [title]
   [main-search]
   [shared-state]
   [list-tags(:tags @app-state)]]
  )

(defn mount-app-element []                                 
  (rdom/render [app] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
