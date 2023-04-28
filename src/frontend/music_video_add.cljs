(ns frontend.music-video-add
  (:require
   [goog.dom :as gdom]
   [goog.events :as gevents]
   [frontend.form :as form]
   [clojure.string :as str]
   [reagent.core :as r :refer [atom]]))

(defonce pasted-link (atom ""))
(defonce player nil)
(defonce music-video-data (atom {:artist-name ""
                                 :title  ""
                                 :duration 0
                                 :youtube-id ""}))

(defn embed-player [youtube-id]
  (let [player-div (gdom/createElement "div")
        button (gdom/createElement "button")]
    (set! (.-id player-div) "player")
    (set! (.-id button) "add-music-video")
    (gdom/setTextContent button "add music video")
    (gdom/appendChild (gdom/getElement "wrapper") button)
    (gevents/listen button "click"
                    (fn [e] (.log js/console "siema")))
    (gdom/appendChild (gdom/getElement "wrapper") player-div)
    (set! player (js/YT.Player. "player" #js {:height "390"
                                              :width "640"
                                              :videoId youtube-id
                                              :events #js {:onReady (fn []
                                                                      (let [[artist title] (map str/trim (str/split (.. (.getVideoData player) -title) #"-"))]
                                                                        (swap! music-video-data assoc :artist-name artist)
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
      (gdom/removeNode (gdom/getElement "add-music-video"))
      (swap! music-video-data assoc-in [:title] "")
      (swap! music-video-data assoc-in [:artist-name] ""))))


(defn update-data [data key]
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
            :value (get @music-video-data :artist-name "")
            :on-change (update-data music-video-data :artist-name)
            :placeholder "artist"}]
   [:input {:id "title"
            :type "text"
            :value (get @music-video-data :title "")
            :on-change (update-data music-video-data :title)
            :placeholder "title"}]
   [:p "https://www.youtube.com/watch?v=WvfEP_1sdtE"]
   [:div#wrapper]])
