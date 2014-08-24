(ns quest-quest.ui
  (:require [quest-quest.quests :refer :all]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(defn make-unit-frames
  "Initialzes the unit frames, starting information is hardcoded."
  []
  (table [:row [(assoc (label (str "HP: " 10)
                              (color :white))
                       :id :health)]
          :row [(assoc (label (str "MP: " 10)
                              (color :white))
                       :id :mana)]
          :row [(assoc (label (str "LVL: " 1)
                              (color :white))
                       :id :level)]]
         :set-position 40 355))

(defn make-quest-table
  [{:keys [title body]}]
  (let [title-label (assoc (label title
                                  (color :white)
                                  :set-scale 1.2 1.2)
                           :id :quest-title)
        body-label (assoc (label body
                                 (color :white))
                          :id :quest-body)]
    (table [:row [title-label]
            :row [body-label]]
           :set-position 400 366)))

(defn make-fps
  []
  (assoc (label "0" (color :white))
         :id :fps
         :x 5))

#_(defn update-all-elements
    [screen entities]
    (refresh-unit-frames)
    (refresh-quest))
