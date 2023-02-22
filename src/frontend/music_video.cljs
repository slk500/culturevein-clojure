(ns frontend.music-video
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
   [frontend.formatter :as f])
  (:refer-clojure :exclude [list]))

(defonce music-video-state (atom {:youtube-id ""
                                  :tags []
                                  :video {}}))

(defn create-youtube-player [youtube-id]
  (js/YT.Player. "player" #js {:height "390"
                               :width "640"
                               :videoId youtube-id}))

(defn show [youtube-id]
  (r/create-class
   {:component-did-mount
    (fn []
      (swap! music-video-state assoc :youtube-id youtube-id)
      (api/get-music-video-tag-list music-video-state youtube-id)
      (api/get-music-video music-video-state youtube-id)
      (create-youtube-player youtube-id))
    :reagent-render
    (fn []
      (let [{:keys [video_name artist_slug artist_name]} (:video @music-video-state)]
        [:div
         [:div#player]
         [:a {:href (str "/artists/" artist_slug)} artist_name]
         [:span " - " video_name]
         [:ul.list-unstyled
          (for [{:keys [video_tag_id tag_name video_tags_time]} (:tags @music-video-state)]
            ^{:key video_tag_id}
            [:li tag_name
             (when video_tags_time
               [:ol
                (for [{:keys [video_tag_time_id start end]} video_tags_time]
                  ^{:key video_tag_time_id}
                  [:li (str (f/seconds-to-time-string start) " -" (f/seconds-to-time-string end))])])])]]))}))

(defn list [artists]
  [:div
   [:ul.list-unstyled.list-break-to-columns
    (for [{:keys [name videos]} artists]
      ^{:key name} [:li name
                    [:ul
                     (for [{:keys [youtube_id name]} videos]
                       ^{:key youtube_id}
                       [:li [:a {:href (str "/music-videos/" youtube_id)} name]])]])]])
