(ns clj-snake.app
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane JLabel JButton)
           (java.awt.event ActionListener KeyListener KeyEvent WindowListener))
  (:gen-class))


;; KONSTANTEN

(def w_width "Fensterbreite" 400)
(def w_height "Fensterhöhe" 400)

(def background-color (Color/CYAN))

(defn say-hello []
  (JOptionPane/showMessageDialog
    nil "Hello, World!" "Greeting"
    JOptionPane/INFORMATION_MESSAGE))

(def act (proxy [ActionListener] []
           (actionPerformed [event] (say-hello))))

(defn spiel []
  "Hier beginnt die Spielkonfiguration"
  (let [frame (JFrame. "Snake von am180")
        panel (JPanel.)
        label (JLabel. "Welcome in our snake game")
        button (JButton. "Start")]
    (doto button
      (.addActionListener act))
    (doto panel
      (.add label)
      (.add button)
      (.setBackground background-color))
    (doto frame
      (.setSize w_width w_height)
      (.setContentPane panel)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.addWindowListener
        (proxy [WindowListener] []
          (windowClosing [evt]
            (println "Whoop"))))
      (.setVisible true))))

(defn -main [& args]
  "Die Hauptfunktion."
  (spiel))