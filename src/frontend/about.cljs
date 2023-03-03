(ns frontend.about)

(defn show []
  [:div
   [:p "Tags are divided into 4 groups:"]
   [:ol
    [:li "red - without exposure time"]
    [:li "red - with exposure time equal to duration of the video. Tag can describe whole video - like lyric video"]
    [:li "orange - with at least one exposure time added but there are more to add"]
    [:li "normal text - with completly all exposure times added"]]])
