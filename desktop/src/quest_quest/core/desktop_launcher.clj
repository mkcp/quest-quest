(ns quest-quest.core.desktop-launcher
  (:require [quest-quest.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. quest-quest "quest-quest" 800 600)
  (Keyboard/enableRepeatEvents true))
