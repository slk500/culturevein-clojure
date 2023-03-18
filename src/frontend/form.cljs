(ns frontend.form)

(defn form-field
([id label] (form-field id label "text"))
  ([id label type]
   [:div {:class "field"}
    [:label {:class "label"} label]
    [:div {:class "control"}
     [:input {:id id
              :type type
              :class "input"}]]]))

(defn action-button [id text]
  [:button {:id id
            :class "button"}
   (str " " text)])
