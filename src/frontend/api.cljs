(ns frontend.api
  (:require
   [secretary.core :as secretary]
   [ajax.core :as ajax]))

(defn get-tag-list [app-state]
  (ajax/GET "http://localhost:8000/api/tags"
    {:handler #(swap! app-state assoc :tags (:data %))
     :response-format :json
     :keywords? true}))

(defn get-tag-show [tag-state tag-slug]
  (ajax/GET (str "http://localhost:8000/api/tags/" tag-slug)
    {:handler #(reset! tag-state (:data %))
     :response-format :json
     :keywords? true}))

(defn get-music-video-list [app-state]
  (ajax/GET "http://localhost:8000/api/videos"
    {:handler #(swap! app-state assoc :music-videos (:data %))
     :response-format :json
     :keywords? true}))

(defn get-music-video-tag-list [music-video-state youtube-id]
  (ajax/GET (str "http://localhost:8000/api/videos/" youtube-id "/tags")
            {:handler #(swap! music-video-state assoc :tags (:data %))
             :response-format :json
             :keywords? true}))

(defn get-music-video-show [music-video-state youtube-id]
  (ajax/GET (str "http://localhost:8000/api/videos/" youtube-id)
    {:handler #(swap! music-video-state assoc :video (first (:data %)))
     :response-format :json
     :keywords? true}))

(defn get-artist-show [artist-state artist-slug]
  (ajax/GET (str "http://localhost:8000/api/artists/" artist-slug)
    {:handler #(reset! artist-state (:data %))
     :response-format :json
     :keywords? true}))

(defn add-music-video [{:keys [youtube-id artist-name title duration]}]
  (ajax/POST (str "http://localhost:8000/api/videos")
    {:params {:youtube_id youtube-id
              :artist_name artist-name
              :video_name title
              :duration duration}
     :format :json
     :on-success (secretary/dispatch! (str "/music-videos/" youtube-id))}))
