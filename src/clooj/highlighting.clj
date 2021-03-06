; Copyright (c) 2011, Arthur Edelstein
; All rights reserved.
; arthuredelstein@gmail.com

(ns clooj.highlighting
  (:import (javax.swing.text DefaultHighlighter
                             DefaultHighlighter$DefaultHighlightPainter)
           (java.awt Color)
           (javax.swing.event CaretListener))
  (:use [clooj.utils :only (awt-event)]
        [clooj.brackets :only (find-bad-brackets find-enclosing-brackets)]))
 
(defn highlight
  ([text-comp start stop color]
    (when (and (<= 0 start) (<= stop (.. text-comp getDocument getLength)))
      (.. text-comp getHighlighter
          (addHighlight start stop
                        (DefaultHighlighter$DefaultHighlightPainter. color)))))
  ([text-comp pos color] (highlight text-comp pos (inc pos) color)))

(defn remove-highlight
  ([text-comp highlight-object]
    (when highlight-object
      (.removeHighlight (.getHighlighter text-comp)
                      highlight-object))))

(defn remove-highlights [text-comp highlights]
    (dorun (map #(remove-highlight text-comp %) highlights)))

(def highlights (atom {}))

(defn highlight-brackets [text-comp good-enclosures bad-brackets]
  (awt-event
    (remove-highlights text-comp (get @highlights text-comp))
    (swap! highlights assoc text-comp
      (doall (concat
               (map #(highlight text-comp % Color/LIGHT_GRAY) good-enclosures)
               (map #(highlight text-comp % Color/PINK) bad-brackets))))))

            