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
      :player (do (c/move-to-middle! screen x y)
                  (when (u/out-of-bounds? y height)
                    (reset-screen!)))))
  entities)

(defn check-level []
  (comment (let [level (:level player)]
             (case (:level player)
               0 (let [quest (level quest)])
               1
               2
               3))))

(defn update-world
  [screen entities]
  (->> entities
       (e/move screen)
       (e/prevent-move screen)
       (e/animate screen)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (->> (orthogonal-tiled-map "world.tmx" (/ 1 u/pixels-per-tile))
         (update! screen :camera (orthographic) :renderer))

    (e/create-player {:image (texture "player.png")
                      :level 1
                      :x 180
                      :y 20})

    #_(e/create-enemy {:image (texture "enemy.png")
                       :level 1
                       :x 150
                       :y 5})
    )

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 1)


    (->> entities
         (map #(update-world screen %))

         #_(run! ui-screen :on-update-ui :entities)

         ;; Apply the transformations to the screen
         (render! screen)
         (update-screen! screen) ))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen u/vertical-tiles)
    nil))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))

    (let [quest (first quests) ; UI screen should get passed a player.

          quest-table (table [:row [(assoc (label (str "Quest: " (:title quest))
                                                  (color :white))
                                           :id :quest-title)]

                              :row [(assoc (label (:body quest)
                                                  (color :white))
                                           :id :quest-body)]]

                             :set-position 800 800)

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
    ; FIXME Update UI with player HP and level every render
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
