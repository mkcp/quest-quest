(ns quest-quest.core.desktop-launcher
  (:require [quest-quest.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(def settings {:name 400 :width 800 :height 400})

(defn -main []
  (let [config (LwjglApplicationConfiguration.)]
    (set! (.title config) (:title settings))
    (set! (.width config) (:width settings))
    (set! (.height config) (:height settings))
    (set! (.vSyncEnabled config) true)
    (LwjglApplication. quest-quest config)
    (Keyboard/enableRepeatEvents true)))

;; Repl helpers
(defn start! []
  (-main)
  (in-ns 'quest-quest.core))
