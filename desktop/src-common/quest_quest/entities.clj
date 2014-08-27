(ns quest-quest.entities
  (:require [quest-quest.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn create-player
  [{:keys [level image x y]}]
  (assoc image
         :id :player
         :x x
         :y y
         :width 2
         :height 2
         :x-velocity 0
         :y-velocity 0
         :level level
         :health (* 10 level)
         :wounds 0
         :can-jump? false
         :direction :left
         :right image
         :left (texture image :flip true false)
         :jump-sound (sound "jump.wav")))

(defn create-enemy
  [{:keys [image level x y id]}]
  (assoc image
         :id id
         :x x
         :y y
         :level level
         :x-velocity 0
         :y-velocity 0
         :width 1
         :height level
         :direction :right
         :health (* 10 level)
         :wounds 0))

(defn level-up
  [screen {:keys [player? level] :as entity}]
  (if player?
    (assoc entity
           :level (inc level))
    entity))

(defn ^:private enable-jump?
  [y-velocity can-jump?]
  (if (pos? y-velocity)
    false
    can-jump?))

(defn move
  [{:keys [delta-time]} {:keys [x y can-jump?] :as entity}]
  (let [x-velocity (u/get-x-velocity entity)
        y-velocity (+ (u/get-y-velocity entity) u/gravity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change)
            (not= 0 y-change))
      (assoc entity
             :x-velocity (u/decelerate x-velocity)
             :y-velocity (u/decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change)
             :can-jump? (enable-jump? y-velocity can-jump?))
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
  [screen {:keys [x-velocity y-velocity
                  right left] :as entity}]
  (let [direction (u/get-direction entity)]
    (merge entity
           (if (= direction :right) right left)
           {:direction direction})))

(defn spawn-all
  "returns a vector containing all of the starting entities"
  []
  (vector (create-player {:image (texture "quester.png") :level 1 :x 20 :y 69})
          (create-enemy {:image (texture "first-enemy.png") :level 1 :id :enemy-first :x 45 :y 10})
          (create-enemy {:image (texture "first-enemy.png") :level 2 :id :enemy-second :x 60 :y 10})
          (create-enemy {:image (texture "first-enemy.png") :level 3 :id :enemy-three :x 75 :y 10})
          (create-enemy {:image (texture "first-enemy.png") :level 10 :id :boss :x 200 :y 80})))
