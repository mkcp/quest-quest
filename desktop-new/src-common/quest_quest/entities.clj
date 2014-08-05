(ns quest-quest.entities
  (:require [quest-quest.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [clojure.pprint :refer :all]))
;; TODO
;; More feedback
;; Add sounds for attacks and bumps
;; Camera linear interpolation to show where the player is headed
;; "I can't do that yet" message

(defn create-player
  "Takes level image x and and y"
  [{:keys [level image x y]}]
  (assoc image
         :right image
         :left (texture image :flip true false)
         :player? true
         :level level
         :width 2
         :height 2
         :x x
         :y y
         :x-velocity 0
         :y-velocity 0
         :health 10
         :mana 10
         :can-jump? false
         :direction :left))

(defn create-enemy
  [{:keys [image level x y]}]
  (assoc image
         :enemy? true
         :level level
         :width 1
         :height level
         :x x
         :y y
         :x-velocity 0
         :y-velocity 0
         :direction :right
         :health (* 10 level)))

(defn move
  [{:keys [delta-time]} {:keys [x y can-jump?] :as entity}]
  (let [x-velocity (u/get-x-velocity entity)
        y-velocity (+ (u/get-y-velocity entity) u/gravity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change) (not= 0 y-change))
      (assoc entity
             :x-velocity (u/decelerate x-velocity)
             :y-velocity (u/decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change)
             :can-jump? (if (> y-velocity 0) false can-jump?))
      entity)))

(defn prevent-move
  [screen {:keys [x y x-change y-change] :as entity}]
    (let [old-x (- x x-change)
          old-y (- y y-change)
          entity-x (assoc entity :y old-y)
          entity-y (assoc entity :x old-x)
          up? (> y-change 0)]
      (merge entity
             (when (u/get-touching-tile screen entity-x "walls")
               {:x-velocity 0 :x-change 0 :x old-x})
             (when-let [tile (u/get-touching-tile screen entity-y "walls")]
               {:y-velocity 0 :y-change 0 :y old-y :can-jump? (not up?)}))))

(defn animate
  [screen {:keys [player? x-velocity y-velocity right left] :as entity}]
  (let [direction (u/get-direction entity)]
    (merge entity
           (if (= direction :right) right left)
           {:direction direction})))

;; FIXME Find a frame where the player is touching the enemy and apply damage.
;;       The player should not be able to attack again without breaking contact. (i.e. ramming multiple times)
#_(defn damage
    [screen entities]
    (if (can-attack? enemy)
      (do (damage-enemy enemy)
          (assoc player
                 {:attack-cooldown? true})
          entities)))
