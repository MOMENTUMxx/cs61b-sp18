package lab11.graphs;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private boolean detectedCycle = false;
    private int[] comeFrom;

    public MazeCycles(Maze m) {
        super(m);
        comeFrom = new int[maze.V()];
    }

    @Override
    public void solve() {
        int startX = 1;
        int startY = 1;
        int s = maze.xyTo1D(startX, startY);

        marked[s] = true;
        comeFrom[s] = s;
        announce();

        dfs(s);
    }

    // Helper methods go here
    private void dfs(int v) {
        if (detectedCycle) {
            return;
        }

        for (int w: maze.adj(v)) {

            if (!marked[w]) {
                marked[w] = true;
                comeFrom[w] = v;
                announce();
                dfs(w);
            }

            if (marked[w] && w != comeFrom[v]) {
                detectedCycle = true;
                comeFrom[w] = v;
                int theOne = v;
                while (theOne != w) {
                    theOne = comeFrom[theOne];
                    edgeTo[theOne] = comeFrom[theOne];
                }
                announce();
                return;
            }
        }
    }
}

