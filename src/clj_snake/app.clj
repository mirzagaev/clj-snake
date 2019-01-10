(ns clj-snake.app
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane JLabel JButton)
           (java.awt.event ActionListener KeyListener KeyEvent WindowListener))
  (:gen-class))


;; KONSTANTEN

(def w_width "Fensterbreite" 400)
(def w_height "Fensterhöhe" 400)

;; COLORs
(def background-color-splash (Color/decode "#DFDCE3"))
(def font-color (Color/decode "#666"))
(def color-white (Color/WHITE))
(def background-color-game (Color/decode "#4ABDAC"))
(def snake-color (Color/decode "#F78733"))
(def apple-color (Color/decode "#FC4A1a"))

(defn say-hello []
  (JOptionPane/showMessageDialog
    nil "Hello, World!" "Greeting"
    JOptionPane/INFORMATION_MESSAGE))

(def start-game (proxy [ActionListener] []
           (actionPerformed [event] (say-hello))))

(defn spiel []
  "Hier beginnt die Spielkonfiguration"
  (let [frame (JFrame. "Snake von am180")
        panel (JPanel.)
        label (JLabel. "Welcome in our snake game")
        button (JButton. "START")]
    (doto button
      (.addActionListener start-game)
      (.setBackground background-color-game))
    (doto panel
      (.setOpaque true)
      (.add label)
      (.add button)
      (.setBackground background-color-splash))
    (doto frame
      (.setSize w_width w_height)
      (.setContentPane panel)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.addWindowListener
        (proxy [WindowListener] []
          (windowClosing [evt]
            (println "Programm geschlossen"))))
      (.setVisible true))))

(defn -main [& args]
  "Die Hauptfunktion."
  (spiel))