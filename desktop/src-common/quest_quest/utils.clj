(ns quest-quest.utils
  (:require [play-clj.core :refer :all]))

(def vertical-tiles 16)
(def pixels-per-tile 16)
(def camera-height 8)
(def duration 0.15)
(def damping 0.5)
(def max-velocity 16)
(def max-jump-velocity 30)
(def gravity -2.5)
(def deceleration 0.9)

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))

(defn ^:private touched?
  [key]
  (and (game :touched?)
       (case key
         :down (> (game :y) (* (game :height) (/ 2 3)))
         :up (< (game :y) (/ (game :height) 3))
         :left (< (game :x) (/ (game :width) 3))
         :right (> (game :x) (* (game :width) (/ 2 3)))
         false)))

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
    (> x-velocity 0) :right
    (< x-velocity 0) :left
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

(defn out-of-bounds?
  [y height]
  (< y (- height)))
