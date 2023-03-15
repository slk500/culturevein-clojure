(ns frontend.music-video-add
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r :refer [atom]]
   [reagent.dom :as rdom]))

(defonce pasted-link (atom ""))
(defonce player (atom nil))

(defn make-div []
  (def player-div (gdom/createElement "div"))
  (set! (.-id player-div) "player")
  (gdom/appendChild (gdom/getElement "wrapper") player-div))

(defn youtube-id [pasted-link]
  (if-some [[_ youtube-id]
            (re-matches #"^(?:https?:)?//[^/]*(?:youtube(?:-nocookie)?\.com|youtu\.be).*[=/]([-\w]{11})(?:\?|=|&|$)"
                        pasted-link)]
    (do (make-div)
        (js/YT.Player. "player" #js {:height "390"
                                     :width "640"
                                     :videoId youtube-id}))
    (gdom/removeNode (gdom/getElement "player"))))

(defn page []
  [:div
   [:p @pasted-link]
   [:input {:type "text"
            :value @pasted-link
            :on-change (fn [evt]
                         (reset! pasted-link (-> evt .-target .-value))
                         (youtube-id (-> evt .-target .-value)))
            :placeholder "paste youtube link here"}]
   [:div#wrapper]])
