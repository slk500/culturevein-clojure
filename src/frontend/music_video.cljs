(ns frontend.music-video
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
   [goog.string :refer [format]]
   ["react-youtube$default" :as YouTube])
  (:refer-clojure :exclude [list]))

(defonce music-video-state (atom {:youtube-id ""
                                  :tags []
                                  :video {}}))

(defn seconds-to-time-string [seconds]
  (let [minutes (quot seconds 60)
        seconds (mod seconds 60)]
    (str (format "%2d" minutes) ":" (format "%02d" seconds))))

(defn show [youtube-id]
  (r/create-class
   {:component-did-mount
    (fn []
      (swap! music-video-state assoc :youtube-id youtube-id)
      (api/get-music-video-tag-list music-video-state youtube-id)
      (api/get-music-video music-video-state youtube-id))
    :reagent-render
    (fn []
      [:div
       [:> YouTube
        {:videoId youtube-id}]
       [:a {:href (str "/artists/" (-> @music-video-state :video :artist_slug))} (-> @music-video-state :video :artist_name)]
       [:span " - " (-> @music-video-state :video :video_name)]
       [:ul {:class "list-unstyled"}
        (for [tag (:tags @music-video-state)]
          ^{:key (:video_tag_id tag)}
          [:li (:tag_name tag)
           (when-let [tags-time (:video_tags_time tag)]
             [:ol
              (for [tag-time tags-time]
                ^{:key (:video_tag_time_id tag-time)}
                [:li (str (seconds-to-time-string (:start tag-time)) " -" (seconds-to-time-string (:end tag-time)))])])])]])}))

(defn list [artists]
  [:div
   [:ul {:class "list-unstyled list-break-to-columns"}
    (for [artist artists]
      ^{:key (:name artist)} [:li (:name artist)
                              [:ul
                               (for [music-video (:videos artist)]
                                 ^{:key (:youtube_id music-video)}
                                 [:li [:a {:href (str "/music-videos/" (:youtube_id music-video))} (:name music-video)]])]])]])

