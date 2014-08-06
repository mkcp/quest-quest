(defproject quest-quest "0.0.11"
  :description "Quest Quest, the easist RPG ever made."

  :dependencies [[com.badlogicgames.gdx/gdx "1.2.0"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.2.0"]
                 [com.badlogicgames.gdx/gdx-box2d "1.2.0"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.2.0"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.2.0"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.2.0"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.2.0"
                  :classifier "natives-desktop"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.trace "0.7.5"]
                 [play-clj "0.3.8"]]

  :source-paths ["src" "src-common"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [quest-quest.core.desktop-launcher]
  :main quest-quest.core.desktop-launcher)
