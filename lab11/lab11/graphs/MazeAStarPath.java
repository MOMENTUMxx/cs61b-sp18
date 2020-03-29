package lab11.graphs;

import edu.princeton.cs.algs4.MinPQ;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MazeAStarPath extends MazeExplorer {

    private class MazeComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer first, Integer second) {
            return Integer.compare(h(first), h(second));
        }
    }

    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private Map<Integer, Integer> hCaches = new HashMap<>();

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return Math.abs(maze.toX(t) - maze.toX(v)) + Math.abs(maze.toY(t) - maze.toY(v));
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex v. */
    private void astar(int v) {
        marked[v] = true;
        MinPQ<Integer> pq = new MinPQ<>(new MazeComparator());
        int cur = v;
        while (!targetFound) {
            for (int w: maze.adj(cur)) {
                if (!marked[w] && w != edgeTo[cur]) {
                    marked[w] = true;
                    edgeTo[w] = cur;
                    distTo[w] = distTo[cur] + 1;
                    if (w == t) {
                        targetFound = true;
                        continue;
                    }
                    pq.insert(w);
                }
            }
            announce();
            cur = pq.delMin();
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

