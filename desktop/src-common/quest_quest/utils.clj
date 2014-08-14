(ns quest-quest.utils
  (:require [play-clj.core :refer :all]))

;;; Screen vars
(def vertical-tiles 12)
(def pixels-per-tile 32)
(def camera-height 6)

;;; Physics vars
(def duration 0.15)
(def damping 0.5)
(def max-velocity 8)
(def max-jump-velocity 24)
(def gravity -1.5)
(def deceleration 0.9)

;;; Input handlers
(defn ^:private touched?
  [key]
  (and (game :touched?)
       (case key
         :down (> (game :y) (* (game :height) (/ 2 3)))
         :up (< (game :y) (/ (game :height) 3))
         :left (< (game :x) (/ (game :width) 3))
         :right (> (game :x) (* (game :width) (/ 2 3)))
         false)))

;;; Camera controls
(defn move-camera!
  "The camera tracks the player if above 8 or 0. It Centers the camera on the world when player is below 8."
  [screen x y]
  (if (< y camera-height)
    (if (pos? y)
      (position! screen x camera-height))
    (position! screen x y)))

;;; World handlers
(defn out-of-bounds?
  [y height]
  (< y (- height)))

;;; Movement handlers
(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))

(defn get-x-velocity
  [{:keys [id x-velocity]}]
  (case id
    :player (cond
              (or (key-pressed? :dpad-left) (touched? :left))
              (* -1 max-velocity)
              (or (key-pressed? :dpad-right) (touched? :right))
              max-velocity
              :else
              x-velocity)
    x-velocity))

(defn get-y-velocity
  [{:keys [id y-velocity can-jump?]}]
  (case id
    :player (cond
              (and can-jump? (or (key-pressed? :dpad-up) (touched? :up)))
              max-jump-velocity
              :else
              y-velocity)
    y-velocity))

(defn get-direction
  [{:keys [x-velocity direction]}]
  (cond
    (pos? x-velocity) :right
    (neg? x-velocity) :left
    :else
    direction))

(defn get-touching-tile
  [screen {:keys [x y width height]} & layer-names]
  (let [layers (map #(tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))

(defn properties->map
  [properties]
  (apply hash-map (interleave (map keyword (.getKeys properties))
                              (.getValues properties))))

(defn reify-checkpoints
  [objects]
  (map (fn [object]
         (let [checkpoint (bean object)
               properties (properties->map (:properties checkpoint))
               new-checkpoint (assoc checkpoint
                                     :x (/ (:x properties) pixels-per-tile)
                                     :y (/ (:y properties) pixels-per-tile))]
           (assoc new-checkpoint
                  :properties properties)))
       objects))

;; FIXME
(defn get-touching-checkpoint
  [screen {:keys [x y width height]} layer-name]
  (->> (map-objects (tiled-map-layer screen layer-name))
       reify-checkpoints 
      ))

(defn near-entity?
  [{:keys [x y id] :as e} e2 min-distance]
  (and (not= id (:id e2))
       (nil? (:draw-time e2))
       (pos? (:health e2))
       (< (Math/abs ^double (- x (:x e2))) min-distance)
       (< (Math/abs ^double (- y (:y e2))) min-distance)))

;; FIXME near should be touching
(defn near-entities?
  [entities entity min-distance]
  (some #(near-entity? entity % min-distance) entities))

;;; Combat handlers
(defn fight [e1 e2])

#_(defn process-fighting
    [e1 e2]
    (when (and (touching? e1 e2)
               (not (have-fought? e1 e2)))
      (fight e1 e2)))

(defn process-damage
  [{:keys [health wounds] :as entity}]
  (assoc entity :health (- health wounds)))
