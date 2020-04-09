import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    static class Node {
        long id;
        double lon;
        double lat;
        Map<String, String> extraInfo;

        Node(long id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
            extraInfo = new HashMap<>();
        }
    }

    static class Edge {
        long id;
        boolean flag;
        List<Long> intersections; //用来表示这条边上有哪些节点
        Map<String, String> extraInfo;

        Edge(long id) {
            this.id = id;
            flag = false;
            intersections = new ArrayList<>();
            extraInfo = new HashMap<>();
        }
    }

    //不能用static修饰因为static修饰的变量优先于对象存在，被所有对象所共享！
    private Map<Long, Set<Long>> adj = new LinkedHashMap<>(); //记录连接关系
    private Map<Long, Node> nodes = new LinkedHashMap<>(); //所有的节点

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Set<Long> toRemove = new HashSet<>();
        for (long id: nodes.keySet()) {
            if (!adj.containsKey(id)) {
                toRemove.add(id); //迭代过程中删除元素会产生concurrentmodificationexception异常，所以用集合记录，稍后一并删除
            }
        }
        removeNode(toRemove);
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return new ArrayList<>(nodes.keySet());
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return adj.get(v);
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double minDistance = Double.MAX_VALUE;
        long toReturn = 0;
        for (Map.Entry<Long, Node> entry: nodes.entrySet()) {
            double currentDistance = distance(lon, lat, entry.getValue().lon, entry.getValue().lat);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                toReturn = entry.getKey();
            }
        }
        return toReturn;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        for (Map.Entry<Long, Node> entry: nodes.entrySet()) {
            if (entry.getKey() == v) {
                return entry.getValue().lon;
            }
        }
        return 0;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        for (Map.Entry<Long, Node> entry: nodes.entrySet()) {
            if (entry.getKey() == v) {
                return entry.getValue().lat;
            }
        }
        return 0;
    }

    void addNode(Node n) {
        nodes.put(n.id, n);
    }

    void removeNode(Set<Long> toRemove) {
        for (long id: toRemove) {
            nodes.remove(id);
        }
    }

    //添加一条边，两个节点彼此相连即为边
    void addEdge(long v, long w) {
        if (!adj.containsKey(v)) {
            adj.put(v, new HashSet<>());
        }
        adj.get(v).add(w);
        if (!adj.containsKey(w)) {
            adj.put(w, new HashSet<>());
        }
        adj.get(w).add(v);
    }

    //添加一条合法的路径，相邻节点之间都用边连起来
    void addWay(Edge e) {
        for (int i = 0; i < e.intersections.size() - 1; i++) {
            addEdge(e.intersections.get(i), e.intersections.get(i + 1));
        }
    }
}
