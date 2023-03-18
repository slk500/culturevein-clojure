(ns frontend.music-video-add
  (:require
   [goog.dom :as gdom]
   [frontend.form :as form]
   [clojure.string :as str]
   [reagent.core :as r :refer [atom]]))

(defonce pasted-link (atom ""))
(defonce player (atom nil))
(defonce music-video-data (atom {:artist ""
                                 :title  ""}))

(defn embed-player [youtube-id]
  (let [player-div (gdom/createElement "div")]
    (set! (.-id player-div) "player")
    (gdom/appendChild (gdom/getElement "wrapper") player-div)
    (set! player (js/YT.Player. "player" #js {:height "390"
                                              :width "640"
                                              :videoId youtube-id
                                              :events #js {:onReady (fn [] (.log js/console "ready"))
                                                           :onStateChange (fn [] (.log js/console "state change"))}}))
    (let [[artist title] (map str/trim (str/split (.. (.getVideoData player) -title) #"-"))]
      (swap! music-video-data assoc :artist artist)
      (swap! music-video-data assoc :title title))))

(defn youtube-id [pasted-link]
  (if-some [[_ youtube-id]
            (re-matches #"^(?:https?:)?//[^/]*(?:youtube(?:-nocookie)?\.com|youtu\.be).*[=/]([-\w]{11})(?:\?|=|&|$)"
                        pasted-link)]
    (embed-player youtube-id)
    (gdom/removeNode (gdom/getElement "player"))))

(defn page []
  [:div
   [:input {:type "text"
            :value @pasted-link
            :on-change (fn [evt]
                         (reset! pasted-link (-> evt .-target .-value))
                         (youtube-id (-> evt .-target .-value)))
            :placeholder "paste youtube link here"}]
   (form/form-field "id" "artist")
   (form/form-field "id" "title")
   (form/action-button "button" "add music video")
   [:div#wrapper]])
