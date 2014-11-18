(ns algols.metrics)

(def sqrt2 (Math/sqrt 2.0))

(defn dist2D-sqr [[x1 y1] [x2 y2]]
  (let [dx (- x2 x1)
        dy (- y2 y1)]
    (+ (* dx dx) (* dy dy))))

(defn dist2D [p1 p2]
  (Math/sqrt (dist2D-sqr p1 p2)))

(defn less-than-dist2D-sqr [d]
  (fn [p1 p2]
    (< (dist2D-sqr p1 p2) d)))

(defn manhatten-dist [[x1 y1] [x2 y2]]
  (+ (Math/abs (- x1 x2)) (Math/abs (- y1 y2))))

(defn chebyshev-dist [[x1 y1] [x2 y2]]
  (Math/max (Math/abs (- x2 x1)) (Math/abs (- y2 y1))))

(defn chebyshev-diagonal-dist [[x1 y1] [x2 y2]]
  (let [diag (Math/min (Math/abs (- x2 x1)) (Math/abs (- y2 y1)))
        manhatten (manhatten-dist [x1 y1] [x2 y2])]
    (+ (* sqrt2 diag) (- manhatten (* 2 diag)))))

