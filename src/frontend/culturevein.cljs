(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
  [goog.dom :as gdom]
  [reagent.core :as r :refer [atom]]
  [reagent.dom :as rdom]
  [ajax.core :as ajax]
  [hiccups.runtime]
  [clojure.string :as str]))

(def value (r/atom ""))
(defonce app-state (atom {:title "CultureVein"
                          :tags {}}))

(defn handle-response [resp]
  (swap! app-state assoc :tags (clojure.walk/keywordize-keys (get-in resp ["data"]))))

(defn get-tags []
  (ajax/GET "http://localhost:8000/api/tags"
            {:handler handle-response}))

(get-tags)

(defn title []
  [:h1 (:title @app-state)])
 
(defn main-search []
  [:div
   [:input {:type "text"
            :value @value
            :on-change #(reset! value (-> % .-target .-value))}]
   [:p "the value is now: " @value]]
)

(defn includes-in-tags-tree? [tags substr]
  (->> (tree-seq associative? identity tags)
       (some #(and (map-entry? %)
                   (let [[k v] %]
                     (and (= k :tag_name_lowercase)
                          (str/includes? v substr)))))))

(defn tags-to-html-list [tags]
  (hiccups/html
   [:ul
    (for [tag tags]  
      [:li 
       (str (:tag_name tag) 
            (if (seq (:children tag))
              (tags-to-html-list (:children tag))))
       ])
    ]))

(defn list-tags [items]
  (let [results (for [item items
                      :when (includes-in-tags-tree? item @value)] item)]
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list results)}}]
  ))

(defn app []
  [:div.app
   [title]
   [main-search]
   [list-tags (:tags @app-state)]
 ]
  )

(defn mount-app-element []                                 
  (rdom/render [app] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
