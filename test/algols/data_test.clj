(ns algols.data-test
  (:use clojure.test)
  (:require [algols.data :as data]))

(deftest simple-delta
  (let [m1 {:a 1}
        m2 {:b 2}
        m1->m2 (data/deltas m1 m2)]
    (testing
      (testing "simple delta"
        (is (= m2 (data/apply-deltas m1 m1->m2)))))))

(deftest simple-change
  (let [m1 {:a 1}
        m2 {:a 2}
        m1->m2 (data/deltas m1 m2)]
    (testing
      (testing "simple delta"
        (is (= m2 (data/apply-deltas m1 m1->m2)))))))


