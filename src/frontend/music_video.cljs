(ns frontend.music-video
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
   ["react-youtube$default" :as YouTube])
  (:refer-clojure :exclude [list]))

(defonce music-video-state (atom {:youtube-id ""
                                  :tags []}))

(defn show [youtube-id]
  (r/create-class
   {:component-did-mount
    (fn []
      (swap! music-video-state assoc :youtube-id youtube-id)
      (api/get-music-video-tag-list music-video-state youtube-id))
    :reagent-render
    (fn []
      [:div
       ;; [:> YouTube
       ;;  {:videoId youtube-id}]
       [:div "ssssssssss"
        [:ul
         (for [tag (:tags @music-video-state)]
           ^{:key (:video-_tag_id tag)}
           [:li (:tag_name tag)])]]])}))

(defn list [artists]
  [:div
   [:ul {:class "list-unstyled list-break-to-columns"}
    (for [artist artists]
      ^{:key (:name artist)} [:li (:name artist)
                              [:ul
                               (for [music-video (:videos artist)]
                                 ^{:key (:youtube_id music-video)}
                                 [:li [:a {:href (str "/music-videos/" (:youtube_id music-video))} (:name music-video)]])]])]])

