(ns algols.search.producers)

(defn make-4-way-brancher [delta]
  (assert (> delta 0))
  (fn [[x y]]
    [[(+ x delta) y]
     [(- x delta) y]
     [x (+ y delta)]
     [x (- y delta)]]))

(defn make-8-way-brancher [delta]
  (assert (> delta 0))
  (fn [[x y]]
    [[(+ x delta) (+ y delta)]
     [(- x delta) (+ y delta)]
     [(+ x delta) (- y delta)]
     [(- x delta) (- y delta)]
     [(+ x delta) y]
     [(- x delta) y]
     [x (+ y delta)]
     [x (- y delta)]]))