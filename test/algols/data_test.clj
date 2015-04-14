(ns algols.data-test
  (:use clojure.test)
  (:require [algols.data :as data]
            [clojure.test.check.clojure-test :as ct]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as props]))

(defn- map-container [inner-type]
  (gen/frequency
    [[3 (gen/vector inner-type)]
     [2 (gen/list inner-type)]
     [1 (gen/fmap set (gen/vector inner-type))]
     [15 (gen/map inner-type gen/simple-type-printable)]]))

(defn- build-tree []
  (gen/such-that map? (gen/recursive-gen map-container gen/simple-type-printable)))

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

(ct/defspec deltas-application-to-src-map-yields-second-map
            100
            (props/for-all [m1 (build-tree)
                            m2 (build-tree)]
                           (let [m1->m2 (data/deltas m1 m2)]
                             (= m2 (data/apply-deltas m1 m1->m2)))))

