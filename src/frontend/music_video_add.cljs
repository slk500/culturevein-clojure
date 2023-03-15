(ns frontend.music-video-add
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]))

(defonce pasted-link (atom ""))
(defonce player (atom nil))

(defn make-player [youtube-id]
  (def player-div (gdom/createElement "div"))
  (set! (.-id player-div) "player")
  (gdom/appendChild (gdom/getElement "wrapper") player-div)
  (js/YT.Player. "player" #js {:height "390"
                               :width "640"
                               :videoId youtube-id}))

(defn youtube-id [pasted-link]
  (if-some [[_ youtube-id]
            (re-matches #"^(?:https?:)?//[^/]*(?:youtube(?:-nocookie)?\.com|youtu\.be).*[=/]([-\w]{11})(?:\?|=|&|$)"
                        pasted-link)]
    (make-player youtube-id)
    (gdom/removeNode (gdom/getElement "player"))))

(defn page []
  [:div
   [:input {:type "text"
            :value @pasted-link
            :on-change (fn [evt]
                         (reset! pasted-link (-> evt .-target .-value))
                         (youtube-id (-> evt .-target .-value)))
            :placeholder "paste youtube link here"}]
   [:div#wrapper]])
