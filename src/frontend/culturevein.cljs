(ns ^:figwheel-hooks frontend.culturevein
  (:require
   [accountant.core :as accountant]
   [frontend.about :as about]
   [frontend.api :as api]
   [frontend.artist :as artist]
   [frontend.layout :as layout]
   [frontend.music-video :as music-video]
   [frontend.music-video-add :as music-video-add]
   [frontend.tag :as tag]
   [goog.dom :as gdom]
   [hiccups.runtime]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]
   [secretary.core :as secretary :refer-macros [defroute]]))

(declare mount-element)

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []}))

(defroute "/about" {}
  (mount-element #(about/show) "app"))

(defroute "/add" {}
  (mount-element #(music-video-add/page) "app"))

(defroute "/artists/:artist-slug" [artist-slug]
  (mount-element #(artist/show artist-slug) "app"))

(defroute "/music-videos" {}
  (mount-element #(music-video/list (:music-videos @app-state)) "app"))

(defroute "/music-videos/:youtube-id" [youtube-id]
  (mount-element #(music-video/show youtube-id) "app"))

(defroute "/tags" {}
  (mount-element #(tag/list (:tags @app-state) value) "app"))

(defroute "/tags/:tag-slug" [tag-slug]
  (mount-element #(tag/show tag-slug) "app"))

(accountant/configure-navigation!
 {:nav-handler   (fn [path] (secretary/dispatch! path))
  :path-exists?  (fn [path] (secretary/locate-route path))})

(api/get-tag-list app-state)
(api/get-music-video-list app-state)

(defn mount-element [f id]
  (rdom/render [f] (gdom/getElement id)))

(defn app-components []
  (mount-element #(layout/navbar value) "navbar")
  (mount-element #(music-video/list (:music-videos @app-state)) "app")
  (mount-element #(tag/list (:tags @app-state) value) "app")
;  (mount-element #(music-video-add/page) "app")
  )

(app-components)

(defn ^:after-load on-reload []
  (app-components))
