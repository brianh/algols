# algols

Useful ~~various~~ algorithm

# Search

## A*/Theta*

Functional implementation of the [A*](http://en.wikipedia.org/wiki/A*_search_algorithm) (read A-Star) algorithm.  In truth, this is Theta* but A* can be thought of as
Theta* without the ability to ever bypass/short circuit the current state.

### End Result
                
A* search can be fully described/defined by the following functions:
* State producer - function that, given a state, will produce all states that can be accessed from it
* Heuristic - function that will (under-)estimate the cost associated with getting from a specific
state to the goal state
  * ```` algols.astar/djikstra-heuristic```` (or simply ```` (constantly 0)````) will 
turn A* into Djikstra's search
* Coster - function that will exactly produce the cost of moving from one state to the next (not
necessarily the goal state)
* Goal-reached? - basis function for the search
* Node processor/manipulator - function that applies the heuristic & coster fns and 
manages the costing information for each state/node
  * ```` algols.astar/astar```` is the A* search node manipulator we all know and love
  * ```` algols.astar/theta-star```` allows for skipping/by-passing states by providing a bypassable?
 function that, when ````false```` fails over the the ````astar```` node processor

### Usage

on the way....

### TODO/FUTURE
* Play/test more
* Compare with other A* implementations
* Think about the current implementation (possible improvements to structure, flow, etc)
* Think about breaking apart the ````search ````
  * Would allow partial search (& thereby inspection of current state)
  * Would facilitate searching from both the start & goal state at the same time
* Don't care for how ````Node```` record leaks out.  May be possible to further de-construct the node
 manipulation to allow greater flexibility (worth it?) or simply hide it behind another fn?

## License

Copyright © 2014

Distributed under the Eclipse Public License, the same as Clojure.
