(ns ^:figwheel-hooks frontend.culturevein
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]
   [ajax.core :as ajax]
   [hiccups.runtime]
   [secretary.core :as secretary :refer-macros [defroute]]
   [accountant.core :as accountant]
   [frontend.tags :as tags]
   [clojure.string :as str]))

(declare music-video-list)
(declare music-video-show)
(declare tag-list)
(declare mount-element)

(defroute "/tags" {}
  (mount-element tag-list))

(defroute "/music-videos" {}
  (mount-element music-video-list))

(defroute "/music-videos/:id" {:as params}
  (mount-element music-video-show))

(accountant/configure-navigation!
 {:nav-handler   (fn [path] (secretary/dispatch! path))
  :path-exists?  (fn [path] (secretary/locate-route path))})

(def value (r/atom ""))
(defonce app-state (atom {:title "CultureVein"
                          :tags []
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

(get-tags)
(get-music-videos)

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

(defn music-videos-to-html-list [artists]
  [:ul {:class "list-unstyled list-break-to-columns"}
   (for [artist artists]
     ^{:key (:name artist)} [:li (:name artist)
                             [:ul
                              (for [music-video (:videos artist)]
                                ^{:key (:youtube_id music-video)} [:li (:name music-video)])]])])

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

(defn navbar []
  [:div
   [:h1 (:title @app-state)]
   [:ul {:class "list-unstyled"}
    [:li [:a {:href "/tags"} "tags"]]
    [:li [:a {:href "/music-videos"} "music videos"]]
    [:li {:href "#"} "add music video"]]
   [main-search]])

(defn music-video-list []
  [:div.app
   [navbar]
   [music-videos-to-html-list (:music-videos @app-state)]])

(defn tag-list []
  [:div.app
   [navbar]
   [list-tags (:tags @app-state)]])

(defn tag-show []
  [:div.app
   [navbar]])

(defn mount-element [f]                                 
  (rdom/render [f] (gdom/getElement "app")))

(mount-element tag-list)

(defn ^:after-load on-reload []
  (mount-element tag-list))
