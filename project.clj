(defproject clj-snake "0.1.0-SNAPSHOT"
  :description "snake Spiel mit Swing für GUI"
  :url "https://gitlab.mi.hdm-stuttgart.de/am180/clj-snake"
  :license {:name "GNU GPL, version 3"
            :url "http://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot clj_snake.app
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})