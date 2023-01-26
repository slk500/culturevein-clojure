(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
  [goog.dom :as gdom]
  [reagent.core :as r :refer [atom]]
  [reagent.dom :as rdom]
  [ajax.core :as ajax]
  [hiccups.runtime]
  (clojure.string :as str)))

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
 
(defn shared-state []
  [:div
   [:p "the value is now: " @value]])

(defn main-search []
  [:input {:type "text"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}])

(defn highlight [string search] 
  (clojure.string/replace string 
                          (js/RegExp. (str "("search")") "iu") "<span class='highlight'>$1</span>"))

;; (defn list-tag [tags]
;;   [:ul (doall (for [tag tags]
;;                 [:li 
;;                  {:dangerouslySetInnerHTML {:__html (highlight (:tag_name tag) @value)}}]                
;;                 ))]
;; )

(defn map-to-html-list [mapa]
  (hiccups/html
   [:ul
    (for [tag mapa]
      [:li 
       (str (:tag_name tag) 
            (if (seq (:children tag))
              (map-to-html-list (:children tag))))
       ])
    ]))

(defn list-tag [tags]
  [:ul (for [tag tags]
                [:li (:tag_name tag)])]
  )

(defn list-tags [items]
  (let [results (for [item items
                      :when (str/includes? (:tag_name_lowercase item) @value)] item)]
    [:div
     [:p (count results) " tags"]
     (list-tag results)
]))

(defn app []
  [:div.app
   [title]
   [:div {:dangerouslySetInnerHTML {:__html (map-to-html-list (:tags @app-state))}}]
   [main-search]
   [shared-state]
   [list-tags (:tags @app-state)]
]
  )

(defn mount-app-element []                                 
  (rdom/render [app] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
