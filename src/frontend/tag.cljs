(ns frontend.tag
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
   [frontend.formatter :as f]
   [clojure.string :as str])
  (:refer-clojure :exclude [list]))

(defonce tag-state (atom nil))

(defn highlight [s search]
  (if (str/blank? search)
    s
    (str/replace s
                 (js/RegExp. (str "(" search ")") "iu")
                 "<span class='highlight'>$1</span>")))

(defn includes-in-tags-tree? [tags substr]
  (->> (tree-seq associative? identity tags)
       (some #(when (map-entry? %)
                (let [[k v] %]
                  (when (= k :tag_name_lowercase)
                    (str/includes? v substr)))))))

(defn tags-to-html-list [tags search first-ul-css-class]
  (html
   [:ul {:class first-ul-css-class}
    (for [tag tags]
      [:li
       (let [tag-name (:tag_name tag)
             children-tag (:children tag)]
         [:a {:href (str "/tags/" (:tag_slug_id tag))}
          (str (highlight tag-name @search)
               (when (seq children-tag)
                 (tags-to-html-list children-tag search "")))])])]))

(defn list [tags search]
  (let [tags-filtred (for [tag tags
                           :when (includes-in-tags-tree? tag @search)] tag)]
    [:div {:dangerouslySetInnerHTML
           {:__html (tags-to-html-list tags-filtred search "list-unstyled list-break-to-columns")}}]))


(defn- create-children-tags-links [tags]
  [:span (interpose ", " (for [children-tag (sort-by :name tags)]
                           ^{:key (:name children-tag)}
                           [:a {:href (str "/tags/" (:slug children-tag))} (:name children-tag)]))])

(defn- tags-show-table [music-videos]
  [:table
   [:thead
    [:tr
     [:th "no."]
     [:th "artist - title"]
     [:th "children tags"]
     [:th "tag duration time"]]]
   [:tbody
    (map-indexed (fn [i music-video]
                   ^{:key i}
                   [:tr
                    [:td (inc i)]
                    [:td [:a {:href (str "/music-videos/" (:slug music-video))}
                          (str (:artist music-video) "  -  " (:name music-video))]]
                    [:td (create-children-tags-links (:tags music-video))]
                    [:td (f/seconds-to-time-string (:duration music-video))]])
                 music-videos)]])

(defn show [tag-slug]
  (api/get-tag-show tag-state tag-slug)
  (fn []
    [:div
     [:div.tag-name (:name @tag-state)]
     [tags-show-table (:videos @tag-state)]]))
