(ns frontend.tag
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require
   [reagent.core :as r :refer [atom]]
   [frontend.api :as api]
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
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list tags-filtred search "list-unstyled list-break-to-columns")}}]))

(defn show [tag-slug]
  (api/get-tag-show tag-state tag-slug)
  (fn []
    [:div
     [:div.tag-name (:name @tag-state)]]))
