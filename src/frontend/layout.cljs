(ns frontend.layout)

(defn navbar [value]
  [:div.navbar
   [:h1 [:a {:href "/"} "CultureVein"]]
   [:ul {:class "list-unstyled"}
    [:li [:a {:href "/tags"} "tags"]]
    [:li [:a {:href "/music-videos"} "music videos"]]
    [:li [:a {:href "#"} "add music video"]]]
   [:div
    [:input {:type "text"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}]]])
