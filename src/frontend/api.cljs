(ns frontend.api
  (:require
   [ajax.core :as ajax]))

(defn get-tag-list [app-state]
  (ajax/GET "http://localhost:8000/api/tags"
            {:handler #(swap! app-state assoc :tags (clojure.walk/keywordize-keys (get-in % ["data"]))) }))

(defn get-music-video-list [app-state]
  (ajax/GET "http://localhost:8000/api/videos"
            {:handler #(swap! app-state assoc :music-videos (clojure.walk/keywordize-keys %))}))
