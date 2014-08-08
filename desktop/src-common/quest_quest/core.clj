(ns quest-quest.core
  (:require [quest-quest.entities :as e]
            [quest-quest.utils :as u]
            [quest-quest.ui :as ui]
            [quest-quest.quests :refer :all]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [clojure.pprint :refer :all]))

(declare quest-quest main-screen npc-health-screen ui-screen reset-screen!)

(defn update-screen!
  "Used in the render function to focus the camera on the player and reset the screen if the player goes out of bounds."
  [screen entities]
  (doseq [{:keys [x y height id to-destroy]} entities]
    (case id
      :player (do
                (u/move-camera! screen x y)
                (when (u/out-of-bounds? y height)
                  (reset-screen!)))
      entities))
  entities)

(defn reset-screen!
  "Starts main-screen from scratch"
  []
  (on-gl (set-screen! quest-quest main-screen ui-screen)))

(defn process
  "Applies the updates to each entity"
  [screen entities]
  (map #(e/update screen %) entities))

(defscreen main-screen
  :on-show
  (fn [screen entities]

    ;; Create world
    (->> (orthogonal-tiled-map "world.tmx" (/ 1 u/pixels-per-tile))
         (update! screen :camera (orthographic) :renderer))

    (e/spawn-all))

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 1)

    #_(run! ui-screen :on-update-ui :entities entities)

    ;; thread all of the entities through the game logic.
    (->> entities
         (process screen)
         (render! screen)
         (update-screen! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen u/vertical-tiles)
    nil))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))

    ;; FIXME Quest tracker is preloaded with default quests
    (let [quest (first quests)]
      [(ui/make-quest-table quest) (ui/make-unit-frames) (ui/make-fps)]))

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (label! :set-text (str (game :fps))))
             entity))
         (render! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen (:height screen))
    nil)

  :on-update-ui
  (fn [screen entities])
  )

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
  "Repl helper which reloads all namespaces and resets the game, useful for changing code."
  []
  (use
       'quest-quest.entities
       'quest-quest.ui
       'quest-quest.utils
       'quest-quest.quests
       'quest-quest.core
       :reload)
  (reset-screen!))

(defn print-dimensions [screen] (println (:height screen) (:width screen)))

; Allows the repl to catch exceptions and clear the screen.
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! quest-quest blank-screen)))))
