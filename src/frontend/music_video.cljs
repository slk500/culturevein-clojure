(ns frontend.music-video
  (:require
   [reagent.core]
   [frontend.api :as api]
   ["react-youtube$default" :as YouTube])
  (:refer-clojure :exclude [list]))

(defonce music-video (atom {:youtube-id ""
                            :tags []}))

(defn show [youtube-id]
  [:div
   [:> YouTube
    {:videoId youtube-id}]])

(defn list [artists]
  [:div
   [:ul {:class "list-unstyled list-break-to-columns"}
    (for [artist artists]
      ^{:key (:name artist)} [:li (:name artist)
                              [:ul
                               (for [music-video (:videos artist)]
                                 ^{:key (:youtube_id music-video)}
                                 [:li [:a {:href (str "/music-videos/" (:youtube_id music-video))} (:name music-video)]])]])]])

