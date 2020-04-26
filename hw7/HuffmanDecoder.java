import java.util.ArrayList;
import java.util.List;

/**
 * Created by LujieWang on 2020/4/25.
 */
public class HuffmanDecoder {
    public static void main(String[] args) {
        ObjectReader or = new ObjectReader(args[0]);
        Object x = or.readObject();
        Object y = or.readObject();
        Object z = or.readObject();

        BinaryTrie decodingTrie = (BinaryTrie) x;
        int numOfSymbols = (int) y;
        BitSequence hugeBitsequence = (BitSequence) z;

        List<Character> chars = new ArrayList<>();
        while (hugeBitsequence.length() != 0) {
            Match match = decodingTrie.longestPrefixMatch(hugeBitsequence);
            chars.add(match.getSymbol());
            hugeBitsequence = hugeBitsequence.allButFirstNBits(match.getSequence().length());
        }
        char[] originalFile = new char[chars.size()];
        for (int i = 0; i < originalFile.length; i++) {
            originalFile[i] = chars.get(i);
        }
        FileUtils.writeCharArray(args[1], originalFile);
    }
}
