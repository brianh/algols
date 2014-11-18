(ns algols.astar-test
  (:use clojure.test
        algols.astar
        algols.producers
        algols.metrics))

(def astar-4-way (new-astar-search-spec
                   (make-4-way-brancher 1)
                   dist2D
                   dist2D
                   (less-than-dist2D-sqr 0.8)))

;; automatically short cuts (pretend we're moving over a completely flat, empty space...)
(def theta-star-4-way (new-theta-star-search-spec
                        (constantly true)
                        (make-4-way-brancher 1)
                        dist2D
                        dist2D
                        (less-than-dist2D-sqr 0.8)))

(deftest simple-4-astar-axis-aligned
  (let [r (search astar-4-way [10 0] [10 10])]
    (testing
      (testing "Path state count"
        (is (= (count (:path r)) 11)))
      (testing "Number of examined states/nodes"
        (is (= (count (:closed-set r)) 11)))
      (testing "Path contents"
        ;; we don't want to rely on the method of choosing the branches but
        ;; we can be sure that every other point will be on the diagonal
        (is (= (:path r) (map vector (repeat 10) (range 11))))))))

(deftest simple-4-theta-star-axis-aligned
  (let [r (search theta-star-4-way [10 0] [10 10])]
    (testing
      (testing "Path state count"
        (is (= (count (:path r)) 2)))
      (testing "Number of examined states/nodes"
        (is (= (count (:closed-set r)) 11)))
      (testing "Path contents"
        (is (= (:path r) [[10 0] [10 10]]))))))

(defn test-ns-hook []
  (simple-4-astar-axis-aligned)
  (simple-4-theta-star-axis-aligned)
  )