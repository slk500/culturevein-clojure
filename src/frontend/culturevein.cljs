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

(declare music-video-list)
(declare music-video-show)
(declare tag-list)
(declare mount-element)

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []}))
(defonce music-videos (cursor app-state [:music-videos]))

(defroute "/tags" {}
  (mount-element tag-list "app"))

(defroute "/music-videos" {}
  (mount-element #(music-video-list (:music-videos @app-state)) "app"))

(defn music-video-list [music-videos]
  [:div
   [music-video/list music-videos]])

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
                 (js/RegExp. (str "("search")") "iu") "<span class='highlight'>$1</span>")))

(defn includes-in-tags-tree? [tags substr]
  (->> (tree-seq associative? identity tags)
       (some #(if (map-entry? %)
                (let [[k v] %]
                  (if (= k :tag_name_lowercase)
                    (str/includes? v substr)))))))

(defn tags-to-html-list [tags first-ul-css-class]
  (hiccups/html
   [:ul {:class first-ul-css-class}
    (for [tag tags]
      [:li
       (let [tag-name (:tag_name tag)
             children-tag (:children tag)]
         (str (highlight tag-name @value)
              (if (seq children-tag)
                (tags-to-html-list children-tag ""))))])]))

(defn list-tags [items]
  (let [results (for [item items
                      :when (includes-in-tags-tree? item @value)] item)]
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list results "list-unstyled list-break-to-columns")}}]))

(defn tag-list []
  [:div
   [list-tags (:tags @app-state)]])

(defn mount-element [f id]                                 
  (rdom/render [f] (gdom/getElement id)))

(defn app-components []
  (do
    (mount-element #(layout/navbar value) "navbar")
    (mount-element tag-list "app")))

(app-components)

(defn ^:after-load on-reload []
  (app-components))
