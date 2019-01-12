(ns clj-snake.app
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane JLabel JButton JTextField)
           (java.awt.event ActionListener KeyListener KeyEvent WindowListener))
  (:gen-class))


;; KONSTANTEN
(def w_width "Fensterbreite" 550)
(def w_height "Fensterhöhe" 550)

;; SWING OBJEKTE

;; COLORs
(def background-color-splash (Color/decode "#DFDCE3"))
(def font-color (Color/decode "#666"))
(def color-white (Color/WHITE))
(def background-color-game (Color/decode "#4ABDAC"))
(def snake-color (Color/decode "#F78733"))
(def apple-color (Color/decode "#FC4A1a"))
(def text-color (Color/decode "#666666"))

(defn say-hello []
  (JOptionPane/showMessageDialog
    nil "Hello, World!" "Greeting"
    JOptionPane/INFORMATION_MESSAGE))

(def btn-action (proxy [ActionListener] []
                  (actionPerformed [event] (say-hello))))


(defn spiel []
  "Hier beginnt die Spielkonfiguration"
  (let [frame (JFrame. "Snake game")
        splash-panel (JPanel.)
        welcome-label (JLabel. "Welcome in our snake game")
        manual-label (JLabel. "follow this manual before you start the game")
        start-button (JButton. "START")]
    (doto start-button
      (.addActionListener btn-action)
      (.setBackground background-color-game))
    (doto splash-panel
      (.setFocusable true)
      (.add welcome-label)
      (.add manual-label)
      (.add start-button)
      (.setBackground background-color-splash))
    (doto frame
      (.setSize w_width w_height)
      (.setContentPane splash-panel)
      (.setLocationRelativeTo nil)
      (.setResizable false)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.addWindowListener
        (proxy [WindowListener] []
          (windowClosing [evt]
            (println "Programm geschlossen"))))
      (.setVisible true))))

(defn -main [& args]
  "Die Hauptfunktion."
  (spiel))