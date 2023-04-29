(ns frontend.layout)

(defn navbar [value]
  [:div
   [:header
    [:h1 [:a.logo {:href "/"} "CultureVein"]]
    [:nav
     [:ul.list-unstyled.nav-links
      [:li [:a {:href "/tags"} "tags"]]
      [:li [:a {:href "/music-videos"} "music videos"]]
      [:li [:a {:href "/add"} "add music video"]]
      ]]
    [:ul.list-unstyled.nav-links
     [:li [:a {:href "/about"} "about"]]
     [:li [:a {:href "/login"} "login"]]
     ]]
   [:div.search-bar
    [:input {:type "text"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))
             :placeholder "search"}]]])
