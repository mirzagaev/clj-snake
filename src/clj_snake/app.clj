(ns clj-snake.app
  (:import (java.awt Color Dimension Font)
           (javax.swing JPanel JFrame Timer JOptionPane JLabel)
           (java.awt.event ActionListener KeyListener KeyEvent WindowListener))
  (:gen-class))

(defn spiel []
  "Hier beginnt die Spielkonfiguration"
  (let [frame (JFrame. "Snake von am180")
        label (JLabel. "Exit or close")]
    (doto frame
      (.add label)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.addWindowListener
        (proxy [WindowListener] []
          (windowClosing [evt]
            (println "Whoop"))))
      (.setVisible true))))

(defn -main [& args]
  "DIe Hauptfunktion."
  (spiel))