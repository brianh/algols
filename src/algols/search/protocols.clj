(ns algols.search.protocols)

(defprotocol Coster
  (cost [this that] "Cost of going from this state to that state."))

(defprotocol Estimator
  (estimate [this that] "Estimated cost of going from this state to that state."))