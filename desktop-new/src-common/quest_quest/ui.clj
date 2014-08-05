(ns quest-quest.ui
  (:require [quest-quest.quests :refer :all]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(defn make-quest-table
  [{:keys [title body]}]
  (table [:row [(assoc (label (str "Active Quest: " title)
                              (color :white))
                       :quest-title? true)]

          :row [(assoc (label body
                              (color :white))
                       :quest-body? true)]]

         :set-position 1450 850))


;; FIXME Starting information is hardcoded. Need to pull from somewhere 
;; if I don't want to start at level 1 every time.
(defn make-unit-frames
  []
  (table [:row [(assoc (label (str "HP: " 10)
                              (color :white))
                       :health? :true)]
          :row [(assoc (label (str "MP: " 10)
                              (color :white))
                       :mana? true)]
          :row [(assoc (label (str "LVL: " 1)
                              (color :white))
                       :level? true)]]
         :set-position 50 850) )

(defn make-fps
  []
  (assoc (label "0" (color :white))
         :fps? true
         :x 5))
