import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        //获取最近的节点
        long startNode = g.closest(stlon, stlat);
        long endNode = g.closest(destlon, destlat);
        List<Long> movePath = new LinkedList<>();

        //建立TrackNode类，追踪每个节点的父节点，便于寻找最短路径
        class TrackNode {
            long id;
            TrackNode parent;
            double moveDistance;
            Double priority;
            TrackNode(long id, TrackNode parent) {
                this.id = id;
                this.moveDistance = parent == null ? 0 : parent.moveDistance
                        + g.distance(id, parent.id);
                this.priority = this.moveDistance + g.distance(id, endNode); //A*
                this.parent = parent;
            }
        }

        //最小优先队列所用的比较器
        class NodeComparator implements Comparator<TrackNode> {
            @Override
            public int compare(TrackNode first, TrackNode second) {
                return Double.compare(first.priority, second.priority);
            }
        }

        PriorityQueue<TrackNode> pq = new PriorityQueue<>(new NodeComparator());
        //起始节点，其父节点为空
        TrackNode currentNode = new TrackNode(startNode, null);
        Set<Long> marked = new HashSet<>();
        //Java中实现A*算法不能用递归
        while (!(currentNode.id == endNode)) {
            for (long nextNode: g.adjacent(currentNode.id)) {
                if (currentNode.parent == null || !(nextNode == currentNode.parent.id)
                        && !marked.contains(nextNode)) {
                    pq.add(new TrackNode(nextNode, currentNode));
                }
            }
            currentNode = pq.poll();
            marked.add(currentNode.id); //重要！！！将考虑过的节点做标记，这个节点已经是最短路径，不再重复计算
        }
        for (TrackNode n = currentNode; n != null; n = n.parent) {
            movePath.add(0, n.id);
        }
        return movePath;
    }

    /**
     * 辅助函数，获取当前节点和下一个节点所在的路径
     * @param num 节点的序号
     */
    private static String getWay(GraphDB g, List<Long> route, int num) {
        String newWay = null;
        for (GraphDB.Edge e: g.edges.values()) {
            if (e.intersections.contains(route.get(num))
                    && e.intersections.contains(route.get(num + 1))) {
                newWay = e.extraInfo.getOrDefault("name", "");
                break;
            }
        }
        return newWay;
    }

    /**
     * 辅助函数，更新方向
     * @param angle 两段路径的方位角之差，不能大于180度
     */
    private static int updateDirection(double angle) {
        int currentDirection;
        if (angle >= -15 && angle <= 15) {
            currentDirection = NavigationDirection.STRAIGHT;
        } else if (angle >= -30 && angle < -15) {
            currentDirection = NavigationDirection.SLIGHT_LEFT;
        } else if (angle > 15 && angle <= 30) {
            currentDirection = NavigationDirection.SLIGHT_RIGHT;
        } else if (angle >= -100 && angle < -30) {
            currentDirection = NavigationDirection.LEFT;
        } else if (angle > 30 && angle <= 100) {
            currentDirection = NavigationDirection.RIGHT;
        } else if (angle < -100) {
            currentDirection = NavigationDirection.SHARP_LEFT;
        } else {
            currentDirection = NavigationDirection.SHARP_RIGHT;
        }
        return currentDirection;
    }

    /**
     * 更新NavigationDirection对象的各个参数
     */
    private static void setNavigationDirection(NavigationDirection n,
                                               String way, double distance, int direction) {
        n.way = way;
        n.distance = distance;
        n.direction = direction;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        double distance = g.distance(route.get(0), route.get(1)); //单独考虑起点
        int direction = NavigationDirection.START; //起点的方向为START(0)
        int newDirection; //沿最短路径更新方向
        String way = getWay(g, route, 0); //获取起点所在路段的名称
        List<NavigationDirection> toReturn = new LinkedList<>();

        for (int i = 1; i < route.size() - 1; i++) {
            String newWay = getWay(g, route, i);
            double prevBearing = g.bearing(route.get(i - 1), route.get(i)); //前一个位置的方位角
            double currBearing = g.bearing(route.get(i), route.get(i + 1)); //当前位置的方位角
            double angle = currBearing - prevBearing; //差值作为判断方向更新的依据
            //角度不能超过180
            if (Math.abs(angle) > 180) {
                prevBearing = g.bearing(route.get(i), route.get(i - 1));
                currBearing = g.bearing(route.get(i + 1), route.get(i));
            }
            angle = currBearing - prevBearing;
            newDirection = updateDirection(angle);
            //如果进入不同的路段，则创建并添加一个NavigationDirection对象，并更新其参数
            if (!newWay.equals(way)) {
                NavigationDirection n = new NavigationDirection();
                setNavigationDirection(n, way, distance, direction);
                toReturn.add(n);
                distance = 0.0;
                way = newWay;
                direction = newDirection;
            }
            double newDistance = g.distance(route.get(i), route.get(i + 1));
            distance += newDistance;
        }
        //单独考虑终点
        NavigationDirection n = new NavigationDirection();
        setNavigationDirection(n, way, distance, direction);
        toReturn.add(n);
        return toReturn;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
