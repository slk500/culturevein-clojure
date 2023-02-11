(ns frontend.layout)

(defn navbar [value]
  [:div
   [:h1 [:a {:href "/"} "CultureVein"]]
   [:ul {:class "list-unstyled"}
    [:li [:a {:href "/tags"} "tags"]]
    [:li [:a {:href "/music-videos"} "music videos"]]
    [:li {:href "#"} "add music video"]]
   [:div
    [:input {:type "text"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}]]
   ])
