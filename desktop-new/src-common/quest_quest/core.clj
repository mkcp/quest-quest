(ns quest-quest.core
  (:require [quest-quest.entities :as e]
            [quest-quest.utils :as u]
            [quest-quest.ui :as ui]
            [quest-quest.quests :refer :all]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [clojure.pprint :refer :all]))

(declare quest-quest main-screen npc-health-screen ui-screen reset-screen! reload!)

;; FIXME Break off into camera component if camera grows more complex.
(defn move-camera!
  "The camera tracks the player if above 8 or 0. It Centers the camera on the world when player is below 8."
  [screen x y]
  (if (< y 8)
    (if (> y 0)
      (position! screen x 8))
    (position! screen x y)))

(defn update-screen!
  "Used in the render function to focus the camera on the player and reset the screen if the player goes out of bounds."
  [screen entities]
  (doseq [{:keys [x y height player? to-destroy]} entities]
    (if player?
      (do (move-camera! screen x y)
          (when (u/out-of-bounds? y height)
            (reset-screen!)))
      entities)
    entities))

(defn reset-screen!
  "Used to reset the game to its initial state"
  []
  (on-gl (set-screen! quest-quest main-screen ui-screen)))

(defscreen main-screen
  :on-show
  (fn [screen entities]

    ;; Load the world map as an orthoganal-tiled-map and add it to the screen with update!
    (->> (orthogonal-tiled-map "world.tmx" (/ 1 u/pixels-per-tile))
         (update! screen :camera (orthographic) :renderer))

    (let [player (e/create-player {:image (texture "quester.png")
                                   :level 1
                                   :x 100
                                   :y 4})
          enemy-one (e/create-enemy {:image (texture "first-enemy.png")
                                     :level 1
                                     :x 15
                                     :y 20})
          enemy-two (e/create-enemy {:image (texture "first-enemy.png")
                                     :level 2
                                     :x 20
                                     :y 20})
          enemy-three (e/create-enemy {:image (texture "first-enemy.png")
                                       :level 3
                                       :x 30
                                       :y 20})]
      ;; FIXME Add boss on top of stair temple thingy
      [player]))

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 1)

    (->> entities
         (map #(->> %
                    (e/move screen)
                    #_(e/prevent-move screen)
                    (e/animate screen)))
         (render! screen)
         (update-screen! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen u/vertical-tiles)
    nil))

(defn- make-ui-elements
  []
  (let [quest (first quests)]
    [#_(ui/make-quest-table quest)
     #_(ui/make-unit-frames)
     (ui/make-fps)]))


(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (make-ui-elements))

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (do (when (:fps? entity)
                 (doto entity (label! :set-text (str (game :fps)))))
               #_(if (:health? entity)
                   (doto entity (label! :set-text (str (:health player))))
                   entity)
               #_(if (:mana? entity)
                   (doto entity (label! :set-text (str (:mana player))))
                   entity)
               #_(if (:level? entity)
                   (doto entity (label! :set-text (str (:level player))))
                   entity)
               #_(if (:quest-title entity)
                   (doto entity (label! :set-text (str (:title (nth (:level player) quests)))))
                   entity)
               #_(if (:quest-title entity)
                   (doto entity (label! :set-text (str (:body (nth (:level player) quests)))))
                   entity)))
         (render! screen) ,,,))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    ; FIXME Width automatically flexes to maintain ratio
    (height! screen (:height screen)))

  :on-update-ui
  (fn [screen entities]
    ; FIXME Update UI with player HP and level every render
    ;       Update quest using player level to set current quest
  ))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defgame quest-quest
  :on-create
  (fn [this]
    (set-screen! this main-screen ui-screen)))


;; REPL Helpers ;;;;;;;;;
(defn reload! []
  (use
       'quest-quest.entities
       'quest-quest.ui
       'quest-quest.utils
       'quest-quest.quests
       'quest-quest.core
       :reload)

  (reset-screen!)
  "reloaded...")

; Allows the repl to catch exceptions and clear the screen.
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! quest-quest blank-screen)))))
;; End REPL helpers ;;;;;;;;;
