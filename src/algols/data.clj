(ns algols.data
  (:require [clojure.set :as set]
            [clojure.core.incubator :refer [dissoc-in]]))

(declare find-deltas)

(defn vectorize-keys
  ([m path]
    (reduce-kv (fn [m k v]
                 (if (map? v)
                   (merge m (vectorize-keys v (conj path k)))
                   (assoc m (conj path k) v)))
               {}
               m))
  ([m]
    (vectorize-keys m [])))

(defn- delta-associative-key
  "Diff things a and a', comparing keys and their values."
  [a a' k]
  (let [va (get a k)
        va' (get a' k)
        [new* chg* del*] (find-deltas va va')
        in-a (contains? a k)
        in-a' (contains? a' k)
        changed (and in-a in-a'
                     (or (not (nil? chg*))
                         (and (or (not (map? va))
                                  (not (map? va')))
                              (not (nil? va))
                              (not= va va'))))]
    [(when (and in-a' (or (not (nil? new*))
                          (not in-a))) {k (or new* va')})
     (when changed {k (or chg* va')})
     (when (and in-a (or (not (nil? del*))
                         (not in-a'))) {k (or del* va)})
     ]))

(defn- delta-associative
  "Diff things a and b, comparing only keys in ks."
  [a b ks]
  (reduce
    (fn [diff1 diff2]
      (doall (map merge diff1 diff2)))
    [nil nil nil]
    (map
      (partial delta-associative-key a b)
      ks)))

(defn- find-deltas [a a']
  (if (or (or (not (map? a)) (not (map? a')))
          (= a a'))
    [nil nil nil]
    (delta-associative a a' (set/union (keys a) (keys a')))))

(defn deltas [a a']
  (zipmap [:added :changed :removed]
          (find-deltas a a')))

(defn apply-deltas [m {:keys [added changed removed]}]
  (let [rm (vectorize-keys removed)
        step1 (apply clojure.core.incubator/dissoc-in m (or (keys rm) []))
        addm (vectorize-keys (or added {}))
        step2 (reduce-kv (fn [m ks v]
                           (assoc-in m ks v)) step1 addm)
        chgm (vectorize-keys (or changed {}))]
    (reduce-kv (fn [m ks v]
                 (update-in m ks (fn [_ arg]
                                   arg) v)) step2 chgm)))

