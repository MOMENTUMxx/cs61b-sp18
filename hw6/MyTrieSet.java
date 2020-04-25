/*
 * 该类引用自github上的一位大佬，在此表示感谢！
 * @source https://github.com/zangsy/cs61b_sp19/blob/master/proj2c/bearmaps/lab9/MyTrieSet.java
 */

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class MyTrieSet implements TrieSet61B {

    private TrieNode root;

    public MyTrieSet() {
        root = new TrieNode('\0', false);
    }

    /** Clears all items out of Trie */
    @Override
    public void clear() {
        root = null;
    }

    /** Returns true if the Trie contains KEY, false otherwise */
    @Override
    public boolean contains(String key) {
        if (key == null || key.length() == 0 || root == null) {
            return false;
        }
        TrieNode currNode = root;
        TrieNode nextNode = null;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            nextNode = currNode.children.get(c);
            if (nextNode == null) {
                return false;
            }
            currNode = nextNode;
        }
        return currNode.isLeaf;
    }

    /** Returns true if the Trie contains prefix, false otherwise */
    public boolean containsPrefix(String prefix) {
        TrieNode pointer = root;

        for (int i = 0; i < prefix.length(); i++) {
            char currentChar = prefix.charAt(i);
            if (!pointer.children.containsKey(currentChar)) {
                return false;
            }

            pointer = pointer.children.get(currentChar);
        }

        return true;
    }

    /** Inserts string KEY into Trie */
    @Override
    public void add(String key) {
        if (key == null || key.length() == 0 || root == null) {
            return;
        }
        TrieNode currNode = root;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            if (!currNode.children.containsKey(c)) {
                currNode.children.put(c, new TrieNode(c, false));
            }
            currNode = currNode.children.get(c);
        }
        currNode.isLeaf = true;
    }

    /** Returns a list of all words that start with PREFIX */
    @Override
    public List<String> keysWithPrefix(String prefix) {
        if (prefix == null || prefix.length() == 0 || root == null) {
            throw new IllegalArgumentException();
        }
        List<String> result = new ArrayList<>();
        TrieNode startNode = root;
        for (int i = 0; i < prefix.length(); i += 1) {
            char c = prefix.charAt(i);
            startNode = startNode.children.get(c);
        }
        // If prefix itself is a key, add it to the result.
        if (startNode.isLeaf) {
            result.add(prefix);
        }
        for (TrieNode currNode : startNode.children.values()) {
            if (currNode != null) {
                keysWithPrefix(result, prefix, currNode);
            }
        }
        return result;
    }

    private void keysWithPrefix(List<String> result, String word, TrieNode currNode) {
        if (currNode.isLeaf) {
            result.add(word + currNode.nodeChar);
        }
        for (TrieNode nextNode : currNode.children.values()) {
            if (nextNode != null) {
                keysWithPrefix(result, word + currNode.nodeChar, nextNode);
            }
        }
    }

    /** Returns the longest prefix of KEY that exists in the Trie
     * Not required for Lab 9. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public String longestPrefixOf(String key) {
        StringBuilder longestPrefix = new StringBuilder();
        TrieNode currNode = root;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            if (!currNode.children.containsKey(c)) {
                return longestPrefix.toString();
            } else {
                longestPrefix.append(c);
                currNode = currNode.children.get(c);
            }
        }
        return longestPrefix.toString();
    }

    private class TrieNode {
        private char nodeChar;
        private boolean isLeaf; // If it is a leaf node, then the nodeChar is a key.
        private Map<Character, TrieNode> children;

        TrieNode(char nodeChar, boolean isLeaf) {
            this.nodeChar = nodeChar;
            children = new HashMap<>();
            this.isLeaf = isLeaf;
        }

    }
}
