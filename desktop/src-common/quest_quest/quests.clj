(ns quest-quest.quests)

(def quests
  [{:title "Safety First"
    :body "The ground is fast approaching, you must land safely!"
    :reward "Move Right Unlocked!"}

   {:title "Right of Way"
    :body "Get a feel for your surroundings, press d or right arrow to move right as far as your legs will carry you!"
    :reward "Move Left Unlocked!"}

   {:title "Left Alone"
    :body "Continue to explore your new surroundings, press press a or left arrow to move left!"
    :reward "Attack Unlocked!"}

   {:title "A Trial By Combat"
    :body "Your first enemy blocks the path! You must defeat it to continue. Apply everything you've learned so far to vanquish this beast!"
    :reward "Jump Unlocked!"}

   {:title "Launch Over It!"
    :body "Tighten the muscles in your legs to form a spring and launch yourself over the rock."
    :reward "Unlocked!"}

   {:title "Defeat the Epic Raid Boss"
    :body "Gather you epic loot"
    :reward "You can now pick up items, press"}

   {:title "The Raid Boss"
    :body "Gather you epic loot"
    :reward ""}])

(defn create-checkpoint [{:keys [x y]}]
  (let [quest (first quests)]
    (assoc quest
           :x x
           :y y)))
