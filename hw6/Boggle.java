import edu.princeton.cs.algs4.In;

import java.util.Stack;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Boggle {

    //按长度递减的顺序排序字符串，长度相同时，按字母表顺序排序
    private static class SearchWordComparator implements Comparator<String> {
        @Override
        public int compare(String first, String second) {
            if (first.length() > second.length()) {
                return -1;
            } else if (first.length() < second.length()) {
                return 1;
            } else {
                return Integer.compare(first.compareTo(second), 0);
            }
        }
    }

    //该类的实例对应Board中的一个字母
    private static class Letter {
        int row; //在矩阵中的行数
        int col; //在矩阵中的列数
        char value; //字母的值
        Letter(int row, int col, char value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        //仅当两个字母对象在Board中的位置及值相同时，两个对象才相同
        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            }
            Letter oLetter = (Letter) o;
            return row == oLetter.row && col == oLetter.col && value == oLetter.value;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }

    // File path of dictionary file
    static String dictPath;
    static List<String> answers;
    static Set<String> answerSet;

    public Boggle() {
        dictPath = "";
        answers = new ArrayList<>();
        answerSet = new HashSet<>();
    }

    /**
     * Solves a Boggle puzzle.
     * 
     * @param k The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     *         The Strings are sorted in descending order of length.
     *         If multiple words have the same length,
     *         have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) {
        // YOUR CODE HERE
        validate(k, boardFilePath);
        In inDict = new In(dictPath);
        MyTrieSet trie = new MyTrieSet();
        char[][] boardMatrix = boardToCharArray(boardFilePath);

        while (inDict.hasNextLine()) {
            trie.add(inDict.readLine());
        }
        inDict.close();
        Stack<Map<String, Object>> stack = new Stack<>();
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[0].length; j++) {
                Map<String, Object> helperMap = new HashMap<>();
                helperMap.put("letter", new Letter(i, j, boardMatrix[i][j]));
                helperMap.put("word", "" + boardMatrix[i][j]);
                helperMap.put("visited", new HashSet<Letter>());
                stack.push(helperMap);

                while (!stack.isEmpty()) {
                    helperMap = stack.pop();
                    String word = (String) helperMap.get("word");
                    Letter letter = (Letter) helperMap.get("letter");
                    HashSet<Letter> visited =
                            new HashSet<>((HashSet<Letter>) helperMap.get("visited"));
                    visited.add(letter);

                    if (word.length() >= 3 && trie.contains(word)) {
                        addAnswer(word);
                    }

                    if (trie.containsPrefix(word)) {
                        for (Letter neighborLetter: neighbor(boardMatrix, letter.row, letter.col)) {
                            if (neighborLetter == null || visited.contains(neighborLetter)) {
                                continue;
                            }
                            int neighborRow = neighborLetter.row;
                            int neighborCol = neighborLetter.col;
                            helperMap = new HashMap<>();
                            helperMap.put("letter", neighborLetter);
                            helperMap.put("word", word + boardMatrix[neighborRow][neighborCol]);
                            helperMap.put("visited", visited);
                            stack.push(helperMap);
                        }
                    }
                }
            }
        }
        return getAnswers(k);
    }

    private static List<String> getAnswers(int k) {
        answers.sort(new SearchWordComparator());
        List<String> results = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            if (i >= answers.size()) {
                return results;
            }
            results.add(answers.get(i));
        }
        return results;
    }

    private static void addAnswer(String word) {
        if (answerSet.contains(word)) {
            return;
        }
        answers.add(word);
        answerSet.add(word);
    }

    private static List<Letter> neighbor(char[][] boardMatrix, int i, int j) {
        List<Letter> toReturn = new ArrayList<>();
        if (inBounds(boardMatrix, i - 1, j)) {
            toReturn.add(new Letter(i - 1, j, boardMatrix[i - 1][j]));
        }
        if (inBounds(boardMatrix, i + 1, j)) {
            toReturn.add(new Letter(i + 1, j, boardMatrix[i + 1][j]));
        }
        if (inBounds(boardMatrix, i, j - 1)) {
            toReturn.add(new Letter(i, j - 1, boardMatrix[i][j - 1]));
        }
        if (inBounds(boardMatrix, i, j + 1)) {
            toReturn.add(new Letter(i, j + 1, boardMatrix[i][j + 1]));
        }
        if (inBounds(boardMatrix, i - 1, j - 1)) {
            toReturn.add(new Letter(i - 1, j - 1, boardMatrix[i - 1][j - 1]));
        }
        if (inBounds(boardMatrix, i - 1, j + 1)) {
            toReturn.add(new Letter(i - 1, j + 1, boardMatrix[i - 1][j + 1]));
        }
        if (inBounds(boardMatrix, i + 1, j - 1)) {
            toReturn.add(new Letter(i + 1, j - 1, boardMatrix[i + 1][j - 1]));
        }
        if (inBounds(boardMatrix, i + 1, j + 1)) {
            toReturn.add(new Letter(i + 1, j + 1, boardMatrix[i + 1][j + 1]));
        }
        return toReturn;
    }

    private static boolean inBounds(char[][] boardMatrix, int i, int j) {
        return i >= 0 && i < boardMatrix.length && j >= 0 && j < boardMatrix[0].length;
    }

    //将Board转化为二维矩阵
    private static char[][] boardToCharArray(String boardFilePath) {
        In inBoard = new In(boardFilePath);
        char[][] boardMatrix = new char[rowNum(boardFilePath)][colNum(boardFilePath)];
        char[] firstRow = inBoard.readLine().toCharArray();
        boardMatrix[0] = firstRow;
        for (int i = 1; inBoard.hasNextLine(); i++) {
            boardMatrix[i] = inBoard.readLine().toCharArray();
        }
        inBoard.close();
        return boardMatrix;
    }

    //Board的列数
    private static int colNum(String boardFilePath) {
        In in = new In(boardFilePath);
        String[] s = in.readAllLines();
        return s[0].length();
    }

    //Board的行数
    private static int rowNum(String boardFilePath) {
        In in = new In(boardFilePath);
        String[] s = in.readAllLines();
        return s.length;
    }

    private static void validate(int k, String boardFilePath) {
        if (k <= 0) {
            throw new IllegalArgumentException();
        }
        In in = new In(boardFilePath);
        if (!in.exists()) {
            throw new IllegalArgumentException();
        }
        in.close();
        //如果Board非矩形，则抛出异常
        if (!isRectangular(boardFilePath)) {
            throw new IllegalArgumentException();
        }
    }

    private static boolean isRectangular(String boardFilePath) {
        In in = new In(boardFilePath);
        String firstRow = in.readLine();
        int colNum = firstRow.length();
        while (in.hasNextLine()) {
            String row = in.readLine();
            int newColNum = row.length();
            if (newColNum != colNum) {
                in.close();
                return false;
            }
            colNum = newColNum;
        }
        in.close();
        return true;
    }

//    public static void main(String[] args) {
//        System.out.println(solve(7, "exampleBoard.txt"));
//    }
}
