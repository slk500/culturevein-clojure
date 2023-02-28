(ns ^:figwheel-hooks frontend.culturevein
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]
   [hiccups.runtime]
   [secretary.core :as secretary :refer-macros [defroute]]
   [accountant.core :as accountant]
   [frontend.tag :as tag]
   [frontend.music-video :as music-video]
   [frontend.api :as api]
   [frontend.layout :as layout]
   [frontend.artist :as artist]))

(declare mount-element)

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []}))

(defroute "/artists/:artist-slug" [artist-slug]
  (mount-element #(artist/show artist-slug) "app"))

(defroute "/tags" {}
  (mount-element #(tag/list (:tags @app-state) value) "app"))

(defroute "/tags/:tag-slug" [tag-slug]
  (mount-element #(tag/show tag-slug) "app"))

(defroute "/music-videos" {}
  (mount-element #(music-video/list (:music-videos @app-state)) "app"))

(defroute "/music-videos/:youtube-id" [youtube-id]
  (mount-element #(music-video/show youtube-id) "app"))

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
  (mount-element #(artist/show "50-cent") "app")
  (mount-element #(tag/list (:tags @app-state) value) "app"))

(app-components)

(defn ^:after-load on-reload []
  (app-components))
