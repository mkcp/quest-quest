(ns quest-quest.core
  (:require [quest-quest.camera :as c]
            [quest-quest.entities :as e]
            [quest-quest.utils :as u]
            [quest-quest.quests :refer :all]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(declare quest-quest main-screen npc-health-screen ui-screen)

(defn reset-screen!
  []
  (set-screen! quest-quest main-screen ui-screen))

(defn update-screen!
  "Used in the render function to focus the camera on the player and reset the screen if the player goes out of bounds."
  [screen entities]
  (doseq [{:keys [x y height id to-destroy]} entities]
    (case id
      :player (do (c/move-to-point! screen x y)
                  (when (u/out-of-bounds? y height)
                    (reset-screen!)))))
  entities)

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (->> (orthogonal-tiled-map "world.tmx" (/ 1 u/pixels-per-tile))
         (update! screen :camera (orthographic) :renderer))

    (e/create-player {:image (texture "player.png")
                      :level 1
                      :x 180
                      :y 20}))

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 1)

    (comment (case (:level player)
               0 (starting)
               1
               2
               3
               4
               5
               6
               7
               8
               9
               10 ))

    (->> entities
         (map #(->> %
                    (e/move screen)
                    (e/prevent-move screen)
                    #_(e/animate screen) ; FIXME Need to flip between the two textures stored on the entity.
                    ))
         (render! screen)
         (update-screen! screen)

         ; FIXME Pass ui-screen the player
         #_(run! ui-screen :on-update-ui :entities)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen u/vertical-tiles)
    nil))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))

    (let [quest (first quests) ; UI screen should get passed a player.

          quest-table (table [:row [(assoc (label (:title quest)
                                                  (color :white))
                                           :id :quest-title)]

                              :row [(assoc (label (:body quest)
                                                  (color :white))
                                           :id :quest-body)]]

                             :align (align :top)
                             )

          fps (assoc (label "0" (color :white))
                     :id :fps
                     :x 5)]
      [quest-table fps]))

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (label! :set-text (str (game :fps))))
             entity))
         (render! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    ; FIXME Width automatically flexes to maintain ratio
    (height! screen (:height screen)))

  ;:on-update-ui
  ;(fn [screen entities]
    ; FIXME Update UI with player HP MP and level
    ; FIXME Update quest using player level to set current quest
  ;  )
  )

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defgame quest-quest
  :on-create
  (fn [this]
    (set-screen! this main-screen ui-screen)))

; Allows the repl to catch exceptions and clear the screen.
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! quest-quest blank-screen)))))
