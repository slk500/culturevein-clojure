(ns frontend.artist
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
   [frontend.tag :as tag]
   [clojure.string :as str]))

(defonce artist-state (atom nil))

(defn- show-table [music-videos]
  [:table
   [:thead
    [:tr
     [:th "no."]
     [:th "title"]
     [:th "tags"]]]
   [:tbody
    (map-indexed (fn [i music-video]
                   ^{:key i}
                   [:tr
                    [:td (inc i)]
                    [:td [:a {:href (str "/music-videos/" (:slug music-video))} (:name music-video)]]
                    [:td (tag/links (:tags music-video))]])
                 music-videos)]])

(defn- page [{:keys [name videos tags]}]
  [:div
   [:h1.title name]
   [:p.title (tag/links tags)]
   [show-table videos]])

(defn show [artist-slug]
  (r/create-class
   {:component-did-mount
    (fn []
      (api/get-artist-show artist-state artist-slug))
    :reagent-render
    (fn []
     (page @artist-state))}))
