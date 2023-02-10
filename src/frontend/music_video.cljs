(ns frontend.music-video
  (:refer-clojure :exclude [list]))

(defn show [youtube-id]
  [:div])

(defn list [artists]
  [:ul {:class "list-unstyled list-break-to-columns"}
   (for [artist artists]
     ^{:key (:name artist)} [:li (:name artist)
                             [:ul
                              (for [music-video (:videos artist)]
                                ^{:key (:youtube_id music-video)} [:li [:a {:href (str "music-videos/" (:youtube_id music-video))} (:name music-video)]])]])])