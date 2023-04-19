(ns frontend.music-video-add
  (:require
   [goog.dom :as gdom]
   [frontend.form :as form]
   [clojure.string :as str]
   [reagent.core :as r :refer [atom]]))

(defonce pasted-link (atom ""))
(defonce player nil)
(defonce music-video-data (atom {:artist ""
                                 :title  ""
                                 :duration 0}))

(defn embed-player [youtube-id]
  (let [player-div (gdom/createElement "div")]
    (set! (.-id player-div) "player")
    (gdom/appendChild (gdom/getElement "wrapper") player-div)
    (set! player (js/YT.Player. "player" #js {:height "390"
                                              :width "640"
                                              :videoId youtube-id
                                              :events #js {:onReady (fn []
                                                                      (let [[artist title] (map str/trim (str/split (.. (.getVideoData player) -title) #"-"))]
                                                                        (swap! music-video-data assoc :artist artist)
                                                                        (swap! music-video-data assoc :title title))
                                                                      (swap! music-video-data assoc :duration (. player getDuration)))
                                                           :onStateChange (fn [] (.log js/console "state change"))}}))))

(defn youtube-id [pasted-link]
  (if-some [[_ youtube-id]
            (re-matches #"^(?:https?:)?//[^/]*(?:youtube(?:-nocookie)?\.com|youtu\.be).*[=/]([-\w]{11})(?:\?|=|&|$)"
                        pasted-link)]
    (embed-player youtube-id)
    (do
      (gdom/removeNode (gdom/getElement "player"))
      (swap! music-video-data assoc-in [:title] "")
      (swap! music-video-data assoc-in [:artist] ""))))


(defn update-data [data key]                               ;; <1>
  (fn [e]
    (swap! data assoc key (.. e -target -value))))

(defn page []
  [:div
   [:input {:type "text"
            :value @pasted-link
            :on-change (fn [evt]
                         (reset! pasted-link (-> evt .-target .-value))
                         (youtube-id (-> evt .-target .-value)))
            :placeholder "paste youtube link here"}]
   [:input {:id "artist"
            :type "text"
            :value (get @music-video-data :artist "")
            :on-change (update-data music-video-data :artist)
            :placeholder "artist"}]
   [:input {:id "title"
            :type "text"
            :value (get @music-video-data :title "")
            :on-change (update-data music-video-data :title)
            :placeholder "title"}]
   [:p "https://www.youtube.com/watch?v=WvfEP_1sdtE"]
   (form/action-button "button" "add music video")
   [:div#wrapper]])
