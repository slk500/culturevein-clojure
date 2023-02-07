(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]
   [ajax.core :as ajax]
   [hiccups.runtime]
   [bide.core :as b]
   [clojure.string :as str]))

(declare page-music-videos)
(declare mount-app-element)

(def value (r/atom ""))
(defonce app-state (atom {:title "CultureVein"
                          :tags {}
                          :current-route [:home]}))

(defn handle-response [resp]
  (swap! app-state assoc :tags (clojure.walk/keywordize-keys (get-in resp ["data"]))))

(defn get-tags []
  (ajax/GET "http://localhost:8000/api/tags"
            {:handler handle-response}))

(get-tags)

(defn mount-page-music-videos []                                 
  (rdom/render [page-music-videos] (gdom/getElement "app")))

(defn main-search []
  [:div
   [:input {:type "text"
            :value @value
            :on-change #(reset! value (-> % .-target .-value))}]]
  )

(defn highlight [s search]
  (if (str/blank? search)
    s
   (str/replace s 
                 (js/RegExp. (str "("search")") "iu") "<span class='highlight'>$1</span>")))

(defn includes-in-tags-tree? [tags substr]
  (->> (tree-seq associative? identity tags)
       (some #(if (map-entry? %)
                   (let [[k v] %]
                     (if (= k :tag_name_lowercase)
                          (str/includes? v substr)))))))

(defn tags-to-html-list [tags]
  (hiccups/html
   [:ul {:class ".container"}
    (for [tag tags]
      [:li
       (let [tag-name (:tag_name tag)
             children-tag (:children tag)]
         (str (highlight tag-name @value)
              (if (seq children-tag)
                (tags-to-html-list children-tag))))])]))

(defn list-tags [items]
  (let [results (for [item items
                      :when (includes-in-tags-tree? item @value)] item)]
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list results)}}]))

(defn navbar []
  [:div 
   [:h1 (:title @app-state)]
   [:ul
    [:li [:a {:href "#" :on-click mount-app-element} "tags"]]
    [:li [:a {:href "#" :on-click mount-page-music-videos} "music videos"]]
    [:li {:href "#"} "add music video"]]
   [main-search]])

(defn page-music-videos []
  [:div.app
   [navbar]])

(defn page-tags []
  [:div.app
   [navbar]
   [list-tags (:tags @app-state)]])

(defn mount-app-element []                                 
  (rdom/render [page-tags] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
