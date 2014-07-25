(ns quest-quest.camera
  (:require [quest-quest.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn move-to-middle!
  [screen x y]
  (if (> y 0) (position! screen x (/ u/vertical-tiles 2))))

(defn move-to-point!
  [screen x y]
  (position! screen x y))

(defn move-to-point-offset-y!
  [screen x y offset]
  (position! screen x (+ y offset)))
