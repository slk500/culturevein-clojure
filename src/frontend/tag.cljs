(ns frontend.tag
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require
   [hiccups.runtime :as hiccupsrt]
   [clojure.string :as str]))

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
         (str (highlight tag-name @search)
              (when (seq children-tag)
                (tags-to-html-list children-tag search ""))))])]))

(defn tag-list [tags search]
  (let [tags-filtred (for [tag tags
                           :when (includes-in-tags-tree? tag @search)] tag)]
    [:div {:dangerouslySetInnerHTML {:__html (tags-to-html-list tags-filtred search "list-unstyled list-break-to-columns")}}]))


