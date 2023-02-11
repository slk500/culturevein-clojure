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
   [frontend.layout :as layout]))

(declare mount-element)

(defonce value (atom ""))
(defonce app-state (atom {:tags []
                          :music-videos []}))

;; (defroute "/tags" {}
;;   (mount-element tag-list "app"))

(defroute "/music-videos" {}
  (mount-element #(music-video/list (:music-videos @app-state)) "app"))

;; todo
;; (defroute "/music-videos/:id" {:as params}
;;   (mount-element music-video/show "app"))

(accountant/configure-navigation!
 {:nav-handler   (fn [path] (secretary/dispatch! path))
  :path-exists?  (fn [path] (secretary/locate-route path))})

(api/get-tag-list app-state)
(api/get-music-video-list app-state)

(defn mount-element [f id]
  (rdom/render [f] (gdom/getElement id)))

(defn app-components []
  (mount-element #(layout/navbar value) "navbar")
  (mount-element #(tag/tag-list (:tags @app-state) value) "app"))

(app-components)

(defn ^:after-load on-reload []
  (app-components))
