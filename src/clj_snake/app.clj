(ns clj-snake.app
  (:use clojure.pprint)
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane JButton)
           (java.awt.event ActionListener KeyListener KeyEvent))
  (:gen-class))

;; KONSTANTEN
(def c-width   "number of horizontal elements on the court" 30)
(def c-height "number of vertical elements on the court" 30)
(def e-size "size of an element in pixels" 30)
(def p-width (* c-width e-size))
(def p-height (* c-height e-size))
(def millsec 100)
(def dirs      "mapping from even code to direction"
  {KeyEvent/VK_LEFT  [-1  0]
   KeyEvent/VK_RIGHT [ 1  0]
   KeyEvent/VK_UP    [ 0 -1]
   KeyEvent/VK_DOWN [ 0 1]})
(def background-color (Color/decode "#aad751"))
(def snake-color (Color/decode "#c96f2b"))
(def apple-color (Color/decode "#45a163"))
(def text-color (Color/decode "#222222"))

(defn screen-rect [[x y]]
  "Converts a pair of coordinates into x, y, width, and height of a
  rectangle on the screen."
  (map (fn [x] (* e-size x))
       [x y 1 1]))

(def screen-rect (memoize screen-rect)) ; creating 'table' of relations

(defn new-snake []
  "Neuer Schlange generieren"
  {:body  (list [1 1])
   :dir   [1 0]
   :color snake-color})

(defn new-apple []
  "Neuen Apfel generieren"
  {:body  [[(rand-int c-width)
            (rand-int c-height)]]
   :color apple-color})

(defn eats-self? [[head & tail]]
  (contains? (set tail) head))

(defn eats-border? [[[x y]]]
  (or (>= x c-width)
      (>= y c-height)
      (< x 0)
      (< y 0)))

(defn lose? [{body :body}]
  (or (eats-self? body)
      (eats-border? body)))

(defn add-points [[x0 y0] [x1 y1]]
  [(+ x0 x1) (+ y0 y1)])

(defn move [{:keys [body dir] :as snake} grows]
  (assoc snake :body
               (cons (add-points (first body) dir)
                     (if grows body (butlast body)))))

(defn eats-apple? [{[head] :body} {[apple] :body}]
  (= head apple))

(defn turn [snake dir]
  (assoc snake :dir dir))

(defn restart-game [snake apple pause level]
  "Reset the game"
  (dosync
    (ref-set snake (new-snake))
    (ref-set apple (new-apple))
    (ref-set pause true)))

(defn update-direction [snake dir]
  "Updates direction of snake."
  (when dir
    (dosync (alter snake turn dir))))

(defn update-position [snake apple level]
  "Updates positions of snake and apple"
  (dosync
    (if (eats-apple? @snake @apple)
      (do (ref-set apple (new-apple))
          (alter snake move true)
          (swap! level inc)
          (println "Apfel gegessen"))
      )
    (alter snake move false))
  nil)

(defn paint [g {:keys [body color]}]
  "Paints constructions like snake or apple."
  (doseq [[x y w h] (map screen-rect body)]
    (doto g
      (.setColor color)
      (.fillRect x y w h))))

(defn pausiere_das_spiel [pause]
  "Spiel pausieren"
  (println "Pausieren")
  (dosync
    (ref-set pause true)))

(defn setze_das_spiel_fort [pause]
  "Spiel fortsetzen"
  (println "Pause aufgehoben")
  (dosync
    (ref-set pause false)))

(defn intro [g level]
  "Einführung"
  (doto g
    (.setColor text-color)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 30))
    (.drawString "clj-game SNAKE von am180" 20 50)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 20))
    (.drawString "Für START beliebige Taste drücken" 20 100)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 20))
    (.drawString "Mit Leertaste das Spiel pausieren bzw. fortführen" 20 130)
    (.setColor apple-color)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 20))
    (.drawString level 20 180)))

(defn spiel-panel [snake apple level pause timer]
  "Spiel Panel wird erstellt"
  (proxy [JPanel ActionListener KeyListener]
         []                                                 ; superclass constructor arguments
    (getPreferredSize [] (Dimension. p-width p-height))
    (paintComponent [g]
      (proxy-super paintComponent g)
      (if @pause
        (intro g (str "Äpfel: " @level))
        (do
          (paint g @snake)
          (paint g @apple))
        )
      )
    (actionPerformed [e]
      (when-not @pause
        (update-position snake apple level)
        (println "Move"))
      (when (lose? @snake)
        (println "Verloren")
        (restart-game snake apple pause level))
      (.repaint this))
    (keyPressed [e]
      (println "Taste gedrückt")
      (if ( = (.getKeyCode e) 32)
        (if-not @pause
          (pausiere_das_spiel pause)
          (setze_das_spiel_fort pause))
        (do
          (if @pause
            (dosync (ref-set pause false))
            (update-direction snake (dirs (.getKeyCode e))))))
      )
    (windowClosed []
      (System/exit 0))
    (keyReleased [e])
    (keyTyped [e])))

(defn spiel []
  "Hier beginnt die Spielkonfiguration"
  (let [frame (JFrame. "Snake")
        snake (ref (new-snake))
        apple (ref (new-apple))
        level (atom 0)
        pause (ref true)
        timer (Timer. millsec nil)
        panel (spiel-panel snake apple level pause timer)]
    (doto panel
      (.setFocusable true)
      (.addKeyListener panel)
      (.setBackground background-color))
    (doto frame
      (.add panel)
      (.pack)
      (.setLocationRelativeTo nil)
      (.setResizable false)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setVisible true))
    (doto timer
      (.addActionListener panel)
      (.start))
    [snake apple level timer]))

(defn -main [& args]
  "Ausgangsfunktion."
  (spiel))