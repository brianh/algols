(ns algols.search.astar
  (:require [clojure.data.priority-map :as pm]))

(def djikstra-heuristic (constantly 0))

(defrecord Node [val parent start-cost goal-est total-cost])

(defn new-node
  ([val parent]
   (new-node val parent Double/POSITIVE_INFINITY Double/POSITIVE_INFINITY))

  ([val parent start-cost goal-est]
   (->Node val parent start-cost goal-est Double/POSITIVE_INFINITY)))

(defn astar [heuristic coster goal ^Node node ^Node next-node]
  "Node processor that manipulates/updates the search space nodes to reflect the A* algorithm.

  heuristic - fn that estimates the cost of obtaining/getting to the goal
  coster - fn that determines the _actual_ cost of getting from the current state to the next
  goal - that which is to be obtained (hopefully)
  node - current state in the search space
  next-node - next state in the search space"
  (let [start-cost (+ (:start-cost node) (coster (:val node) (:val next-node)))]
    (if (< start-cost (:start-cost next-node))
      (let [goal-est (heuristic (:val next-node) goal)]
        (assoc next-node :parent node
                             :start-cost start-cost
                             :goal-est goal-est
                             :total-cost (+ start-cost goal-est)))
      next-node)))

(defn theta-star [bypassable?]
  "bypassable? - function of 2 states [prior-state next-state] that determines whether the new state
  may be directly connected/associated with the previous state (thereby bypassing the current state).

  The arguments passed to the provided function will be: (bypassable? prev-state next-state)

  When the bypassable? function returns false, processing is handed off to the A* algorithm."
  (fn [heuristic coster goal node next-node]
   (let [parent-node (:parent node)
         parent-state (:val parent-node)
         next-state (:val next-node)]
     (if (and (not (nil? parent-state)) (bypassable? parent-state next-state))
       (let [shortcut-start-cost (+ (:start-cost parent-node) (coster parent-state next-state))]
         (if (< shortcut-start-cost (:start-cost next-node))
           (let [goal-est (heuristic next-state goal)]
             (assoc next-node :parent parent-node
                              :start-cost shortcut-start-cost
                              :goal-est goal-est
                              :total-cost (+ shortcut-start-cost goal-est)))
           next-node))
       (astar heuristic coster goal node next-node)))))

(defrecord SearchSpec [node-processor state-producer heuristic coster goal-reached? open-map closed-set])

(defn new-search-spec
  "Creates a new search specification to be used for a search.

  node-processor - function that manipulates and analyzes nodes (most should simply use
                   the 'astar' or 'theta-star' provided)
  state-producer - function that, given a state in the solution
                   space will return all states that are accessible from it
  heuristic - function that will estimate the cost of going from one state to the _goal_ state
  coster - function that will calculate the actual cost of going from one state to the next
  goal-reached? - function for determining when the desired goal state has been achieved"
  ([node-processor state-producer heuristic coster goal-reached?]
   (new-search-spec node-processor state-producer heuristic coster goal-reached?
                    (pm/priority-map-keyfn-by :total-cost <) #{}))

  ([node-processor state-producer heuristic coster goal-reached? open-map closed-set]
   (->SearchSpec node-processor state-producer heuristic coster goal-reached? open-map closed-set)))

(defn new-astar-search-spec [state-producer heuristic coster goal-reached?]
  (new-search-spec astar state-producer heuristic coster goal-reached?))

(defn new-theta-star-search-spec [bypassable? state-producer heuristic coster goal-reached?]
  (new-search-spec (theta-star bypassable?) state-producer heuristic coster goal-reached?))

(defn- extract-path
  ([last-node]
   (loop [node last-node
          path (list (:val node))]
     (if (nil? (and node (:parent node)))
       (vec path)
       (if-let [parent (:parent node)]
         (recur parent (conj path (:val parent))))))))

(defn search [^SearchSpec search-params start-state goal-state]
  "Search for sequence of states leading from the start state to the goal state."
  (let [{:keys [node-processor state-producer heuristic coster goal-reached? open-map closed-set]} search-params
        start-node (new-node start-state nil 0 (heuristic start-state goal-state))]
    (loop [open open-map
           cur-node start-node
           closed (conj closed-set (:val start-node))]
      (cond (nil? cur-node) nil
        (goal-reached? (:val cur-node) goal-state) (assoc search-params :final-node cur-node :path (extract-path cur-node) :open-map open :closed-set closed)
        :else (let [next-nodes (for [neighbor-state (state-producer (:val cur-node))
                                         :when (not (contains? closed neighbor-state))
                                         :let [s (or (get open neighbor-state)
                                                     (new-node neighbor-state nil))]]
                                     s)
                    avail-nodes (vec (for [avail-node next-nodes
                                           :let [old-start-cost (:start-cost avail-node)
                                                 new-avail-node (node-processor heuristic coster goal-state cur-node avail-node)]
                                           :when (< (:start-cost new-avail-node) old-start-cost)]
                                       new-avail-node))
                    kvs (mapcat (juxt :val identity) avail-nodes)
                    new-open (if (nil? (seq kvs))
                               open
                               (apply assoc open kvs))
                    next-entry (peek new-open)
                    next-node (and next-entry (val next-entry))]
                (recur (pop new-open) next-node (conj closed (:val next-node))))))))
