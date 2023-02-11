(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom cursor]]
   [reagent.dom :as rdom]
   [hiccups.runtime]
   [secretary.core :as secretary :refer-macros [defroute]]
   [accountant.core :as accountant]
   [frontend.tag :as tag]
   [frontend.music-video :as music-video]
   [frontend.api :as api]
   [frontend.layout :as layout]
   [clojure.string :as str]))

(declare mount-element)

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []}))

;; (defroute "/tags" {}
;;   (mount-element tag-list "app"))

(defroute "/music-videos" {}
  (mount-element #(music-video/list (:music-videos @app-state)) "app"))

;; todo
;; (defroute "/music-videos/:id" {:as params}
;;   (mount-element music-video/show "app"))

(accountant/configure-navigation!
 {:nav-handler   (fn [path] (secretary/dispatch! path))
  :path-exists?  (fn [path] (secretary/locate-route path))})

(api/get-tag-list app-state)
(api/get-music-video-list app-state)

(defn highlight [s search]
  (if (str/blank? search)
    s
    (str/replace s
                 (js/RegExp. (str "(" search ")") "iu")
                 "<span class='highlight'>$1</span>")))

(defn includes-in-tags-tree? [tags substr]
  (->> (tree-seq associative? identity tags)
       (some #(when (map-entry? %)
                (let [[k v] %]
                  (when (= k :tag_name_lowercase)
                    (str/includes? v substr)))))))

(defn tags-to-html-list [tags first-ul-css-class]
  (hiccups/html
   [:ul {:class first-ul-css-class}
    (for [tag tags]
      [:li
       (let [tag-name (:tag_name tag)
             children-tag (:children tag)]
         (str (highlight tag-name @value)
              (when (seq children-tag)
                (tags-to-html-list children-tag ""))))])]))

(defn tag-list [tags]
  (let [results (for [tag tags
                      :when (includes-in-tags-tree? tag @value)] tag)]
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list results "list-unstyled list-break-to-columns")}}]))

(defn mount-element [f id]
  (rdom/render [f] (gdom/getElement id)))

(defn app-components []
  (mount-element #(layout/navbar value) "navbar")
  (mount-element #(tag-list (:tags @app-state)) "app"))

(app-components)

(defn ^:after-load on-reload []
  (app-components))
