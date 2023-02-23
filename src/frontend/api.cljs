(ns frontend.api
  (:require
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

(defn get-music-video [music-video-state youtube-id]
  (ajax/GET (str "http://localhost:8000/api/videos/" youtube-id)
            {:handler #(swap! music-video-state assoc :video (first (:data %)))
             :response-format :json
             :keywords? true}))
