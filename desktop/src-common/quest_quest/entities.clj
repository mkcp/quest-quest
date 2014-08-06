(ns quest-quest.entities
  (:require [quest-quest.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn create-player
  [{:keys [level image x y]}]
  (assoc image
         :right image
         :left (texture image :flip true false)
         :width 2
         :height 2
         :x-velocity 0
         :y-velocity 0
         :level level
         :x x
         :y y
         :health (* 10 level)
         :mana (* 10 level)
         :id :player
         :can-jump? false
         :direction :left))

;; FIXME This enemy has too much and too little.
;; FIXME It could be a problem with the functions here in general. Split player and enemy movement up.
(defn create-enemy
  [{:keys [image level x y id ]}]
  (assoc image
         :x x
         :y y
         :id id
         :level level
         :x-velocity 0
         :y-velocity 0
         :width 1
         :height level
         :direction :right
         :health (* 10 level)))

(defn- update-player-position
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
             :can-jump? (if (> y-velocity 0) false can-jump?))
      entity)))


(defn prevent-move-player
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
             {:y-velocity 0 :y-change 0 :y old-y
              :can-jump? (not up?)}))))

(defn animate-player
  [screen {:keys [x-velocity y-velocity
                  right left] :as entity}]
  (let [direction (u/get-direction entity)]
    (merge entity
           (if (= direction :right) right left)
           {:direction direction})))

(defn move-player
  "Calculates the change in x and y by multiplying velocity by time.
  If these are different, the entity is updated."
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
             :can-jump? (if (> y-velocity 0) false can-jump?))
      entity)))
