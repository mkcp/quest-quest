(ns quest-quest.core.desktop-launcher
  (:require [quest-quest.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (let [config (LwjglApplicationConfiguration.)]
    (set! (.title config) "quest-quest")
    (set! (.width config) 800)
    (set! (.height config) 450)
    (set! (.vSyncEnabled config) true)
    (LwjglApplication. quest-quest config)
    (Keyboard/enableRepeatEvents true)))


;; Repl helpers
(defn start! []
  (-main)
  (in-ns 'quest-quest.core))
