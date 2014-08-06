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

(defn update-screen!
  "Used in the render function to focus the camera on the player and reset the screen if the player goes out of bounds."
  [screen entities]
  (doseq [{:keys [x y height id to-destroy]} entities]
    (case id
      :player (do (u/move-camera! screen x y)
                  (when (u/out-of-bounds? y height)
                    (reset-screen!)))
      entities))
  entities)

(defn reset-screen!
  []
  (on-gl (set-screen! quest-quest main-screen ui-screen)))

(defscreen main-screen
  :on-show
  (fn [screen entities]

    ;; Create world
    (->> (orthogonal-tiled-map "world.tmx" (/ 1 u/pixels-per-tile))
         (update! screen :camera (orthographic) :renderer))

    ;; Spawn entities
    (let [player (e/create-player {:image (texture "quester.png")
                                   :level 1
                                   :x 15
                                   :y 80})

          enemy-one (e/create-enemy {:image (texture "first-enemy.png")
                                 :level 1
                                 :id :enemy-first
                                 :x 45
                                 :y 10})

          enemy-two (e/create-enemy {:image (texture "first-enemy.png")
                                     :level 2
                                     :id :enemy-second
                                     :x 60
                                     :y 10})

          enemy-three (e/create-enemy {:image (texture "first-enemy.png")
                                       :level 3
                                       :id :enemy-three
                                       :x 75
                                       :y 10})]

      [player enemy-one enemy-two enemy-three]))

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 1)

    (->> entities
         (map #(->> %
                    (e/move-player screen)
                    (e/prevent-move-player screen)
                    (e/animate-player screen)))

         ;; FIXME Update the UI every render tick
         #_(run! ui-screen :on-update-ui :entities)

         (render! screen)
         (update-screen! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen u/vertical-tiles)
    nil))

(defn- make-ui-elements
  []
  (let [quest (first quests)]
    [(ui/make-quest-table quest) (ui/make-unit-frames) (ui/make-fps)]))


(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (make-ui-elements))

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

  :on-update-ui
  (fn [screen entities]
    ; FIXME Update UI with player HP and level every render
    ; FIXME Update quest using player level to set current quest
  ))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defgame quest-quest
  :on-create
  (fn [this]
    (set-screen! this main-screen ui-screen)))

;; Repl helpers
(defn reload!
  []
  (use
       'quest-quest.entities
       'quest-quest.ui
       'quest-quest.utils
       'quest-quest.quests
       'quest-quest.core
       :reload)

  (reset-screen!))

; Allows the repl to catch exceptions and clear the screen.
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! quest-quest blank-screen)))))
