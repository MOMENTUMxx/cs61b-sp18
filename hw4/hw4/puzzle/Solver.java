package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;


public class Solver {

    private class SearchNode {
        private WorldState state;
        private int moveNum;
        private Integer priority;
        private SearchNode parent;

        private SearchNode(WorldState state, SearchNode parent) {
            this.state = state;
            this.moveNum = parent == null ? 0 : parent.moveNum + 1;
            if (edCaches.containsKey(this.state)) {
                int ed = edCaches.get(this.state);
                this.priority = this.moveNum + ed;
            } else {
                int ed = this.state.estimatedDistanceToGoal();
                edCaches.put(this.state, ed);
                this.priority = this.moveNum + ed;
            }

            this.parent = parent;
        }
    }

    private class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode left, SearchNode right) {
            return left.priority.compareTo(right.priority);
        }
    }

    private Map<WorldState, Integer> edCaches = new HashMap<>(); // Caches estimated distance
    private Stack<WorldState> movePath = new Stack<>();

    /**
     * Constructor which solves the puzzle, computing everything necessary for `moves` and
     * solution` not to have to solve the problem again.
     * Solves the puzzle using the A* algorithm. Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        MinPQ<SearchNode> pq = new MinPQ<>(new SearchNodeComparator());
        SearchNode currentNode = new SearchNode(initial, null);

        while (!currentNode.state.isGoal()) {
            for (WorldState nextState: currentNode.state.neighbors()) {
                if (currentNode.parent == null || !nextState.equals(currentNode.parent.state)) {
                    SearchNode nextNode = new SearchNode(nextState, currentNode);
                    pq.insert(nextNode);
                }
            }
            currentNode = pq.delMin();
        }

        for (SearchNode node = currentNode; node != null; node = node.parent) {
            movePath.push(node.state);
        }
    }

    /**
     * Returns the minimum number of moves to solve the puzzle
     * starting from the initial `WorldState`
     */
    public int moves() {
        return movePath.size() - 1;
    }

    /**
     * Returns a sequence of `WorldState`s from the initial `WorldState` to the solution.
     */
    public Iterable<WorldState> solution() {
        return movePath;
    }
}
