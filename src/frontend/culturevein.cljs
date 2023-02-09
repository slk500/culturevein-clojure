(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom cursor]]
   [reagent.dom :as rdom]
   [ajax.core :as ajax]
   [hiccups.runtime]
   [secretary.core :as secretary :refer-macros [defroute]]
   [accountant.core :as accountant]
   [frontend.tags :as tags]
   [frontend.layout :as layout]
   [clojure.string :as str]))

(declare music-video-list)
(declare music-video-show)
(declare tag-list)
(declare mount-element)

(defroute "/tags" {}
  (mount-element tag-list "app"))

(defroute "/music-videos" {}
  (mount-element music-video-list "app"))

(defroute "/music-videos/:id" {:as params}
  (mount-element music-video-show "app"))

(accountant/configure-navigation!
 {:nav-handler   (fn [path] (secretary/dispatch! path))
  :path-exists?  (fn [path] (secretary/locate-route path))})

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []
                          :current-route [:home]}))

(defn handle-response-tags [resp]
  (swap! app-state assoc :tags (clojure.walk/keywordize-keys (get-in resp ["data"]))))

(defn handle-response-music-videos [resp]
  (swap! app-state assoc :music-videos (clojure.walk/keywordize-keys resp)))

(defn get-tags []
  (ajax/GET "http://localhost:8000/api/tags"
            {:handler handle-response-tags}))

(defn get-music-videos []
  (ajax/GET "http://localhost:8000/api/videos"
            {:handler handle-response-music-videos}))

(defn get-music-video-show [youtube-id]
  (ajax/GET "http://localhost:8000/api/music-videos/"
            {:handler handle-response-music-videos}))

(get-tags)
(get-music-videos)

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

(defn music-videos-to-html-list [artists]
  [:ul {:class "list-unstyled list-break-to-columns"}
   (for [artist artists]
     ^{:key (:name artist)} [:li (:name artist)
                             [:ul
                              (for [music-video (:videos artist)]
                                ^{:key (:youtube_id music-video)} [:li [:a {:href (str "music-videos/" (:youtube_id music-video))} (:name music-video)]])]])])

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

(defn music-video-show [youtube-id]
  [:div])

(defn music-video-list []
  [:div
   [music-videos-to-html-list (:music-videos @app-state)]])

(defn tag-list [name]
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
