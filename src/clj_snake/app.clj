(ns clj-snake.app
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener KeyEvent))
  (:gen-class))


;; KONSTANTEN
(def c-width   "number of horizontal elements on the court" 30)
(def c-height "number of vertical elements on the court" 30)
(def e-size "size of an element in pixels" 20)
(def p-width (* c-width e-size))
(def p-height (* c-height e-size))
(def i-quantum "initial duration of repainting period"      100)
(def d-quantum "change of the duration for two succ levels" -5)
(def m-quantum "limit for the duration"                     50)
(def i-length  "initial length for the snake to win"        5)
(def d-length "change of the length for two succ levels" 3)
(def dirs      "mapping from even code to direction"
  {KeyEvent/VK_LEFT  [-1  0]
   KeyEvent/VK_RIGHT [ 1  0]
   KeyEvent/VK_UP    [ 0 -1]
   KeyEvent/VK_DOWN [ 0 1]})

;; COLORs
(def background-color (Color/decode "#aad751"))
(def snake-color (Color/decode "#3d6adc"))
(def apple-color (Color/decode "#e8481d"))
(def text-color (Color/decode "#222222"))

;;; pure section
(defn quantum [level]
  "Evaluates period of repainting based on level."
  (max (+ i-quantum (* level d-quantum)) m-quantum))

(defn length [level]
  "Evaluates length of the snake that will cause win."
  (+ i-length (* level d-length)))

(def length (memoize length)) ; the function is called every period

(defn screen-rect [[x y]]
  "Converts a pair of coordinates into x, y, width, and height of a
  rectangle on the screen."
  (map (fn [x] (* e-size x))
       [x y 1 1]))

(def screen-rect (memoize screen-rect)) ; creating 'table' of relations


;; ZUFÄLLIG
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

;; functional operations
(defn eats-self? [[head & tail]]
  (contains? (set tail) head))

(defn eats-border? [[[x y]]]
  "true if snake eats the border"
  (or (>= x c-width)
      (>= y c-height)
      (< x 0)
      (< y 0)))

(defn lose? [{body :body}]
  "Snake loses when it eats something but the apple"
  (or (eats-self? body)
      (eats-border? body)))

;; GUI
(defn add-points [[x0 y0] [x1 y1]]
  "Adds two points, used to shift head to the snake."
  [(+ x0 x1) (+ y0 y1)])

(defn move [{:keys [body dir] :as snake} grows]
  "Evaluates snake after one move."
  (assoc snake :body
               (cons (add-points (first body) dir)
                     (if grows body (butlast body)))))

(defn eats-apple? [{[head] :body} {[apple] :body}]
  "True when snake eats apple"
  (= head apple))

(defn turn [snake dir]
  (assoc snake :dir dir))

(defn restart [snake apple pause]
  "Reset the game"
  (dosync
    (ref-set snake (new-snake))
    (ref-set apple (new-apple))
    (ref-set pause true)))

(defn update-dir [snake dir]
  "Updates direction of snake."
  (when dir
    (dosync (alter snake turn dir))))

(defn update-pos [snake apple]
  "Updates positions of snake and apple"
  (dosync
    (if (eats-apple? @snake @apple)
      (do (ref-set apple (new-apple))
          (alter snake move true)
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

(defn introduction [g level]
  "Introduction Content"
  (doto g
    (.setColor text-color)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 30))
    (.drawString "Welcome in our snake game" 20 50)
    (.setFont (Font. "Tahoma" Font/TRUETYPE_FONT 20))
    (.drawString level 40 90)))

(defn spiel-panel [snake apple level pause timer]
  "Spiel Panel wird erstellt"
  (proxy [JPanel ActionListener KeyListener]
         []                                                 ; superclass constructor arguments
    (getPreferredSize [] (Dimension. p-width p-height))
    (paintComponent [g]
      (proxy-super paintComponent g)
      (if @pause
        (introduction g (str "Level: " @level))
        (do (paint g @snake)
            (paint g @apple))
        )
      )
    (actionPerformed [e]
      (when-not @pause
        (update-pos snake apple)
        (println "Move"))
      (when (lose? @snake)
        (println "Verloren")
        (restart snake apple pause))
      (.repaint this))
    (keyPressed [e]
      (println "Taste gedrückt")
      (if @pause
        (dosync (ref-set pause false))
        (update-dir snake (dirs (.getKeyCode e))))
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
        timer (Timer. (quantum @level) nil)
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
  "Die Hauptfunktion."
  (spiel))