import edu.princeton.cs.algs4.MinPQ;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LujieWang on 2020/4/25.
 */
public class BinaryTrie implements Serializable {
    Node root;

    // Huffman trie node
    private static class Node implements Comparable<Node>, Serializable {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    // build the Huffman trie given frequencies
    private Node buildTrie(Map<Character, Integer> frequencyTable) {
        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<>();
        for (char c: frequencyTable.keySet()) {
            pq.insert(new Node(c, frequencyTable.get(c), null, null));
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        root = buildTrie(frequencyTable);
    }

    public Match longestPrefixMatch(BitSequence querySequence) {
        Node currentNode = root;
        BitSequence b = new BitSequence();
        for (int i = 0; i < querySequence.length(); i++) {
            if (querySequence.bitAt(i) == 0) {
                currentNode = currentNode.left;
                b = b.appended(0);
            } else {
                currentNode = currentNode.right;
                b = b.appended(1);
            }
            if (currentNode.isLeaf()) {
                return new Match(b, currentNode.ch);
            }
        }
        return null;
    }

    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> toReturn = new HashMap<>();
        buildLookupTable(toReturn, root, "");
        return toReturn;
    }

    private void buildLookupTable(Map<Character, BitSequence> map, Node n, String s) {
        if (!n.isLeaf()) {
            buildLookupTable(map, n.left, s + '0');
            buildLookupTable(map, n.right, s + '1');
        } else {
            map.put(n.ch, new BitSequence(s));
        }
    }
}
