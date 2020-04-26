import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LujieWang on 2020/4/25.
 */
public class HuffmanEncoder {
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> toReturn = new HashMap<>();
        for (char c: inputSymbols) {
            if (!toReturn.containsKey(c)) {
                toReturn.put(c, 1);
            } else {
                toReturn.put(c, toReturn.get(c) + 1);
            }
        }
        return toReturn;
    }

    public static void main(String[] args) {
        char[] file = FileUtils.readFile(args[0]);
        Map<Character, Integer> frequencyTable = buildFrequencyTable(file);
        BinaryTrie decodingTrie = new BinaryTrie(frequencyTable);
        ObjectWriter ow = new ObjectWriter(args[0] + ".huf");
        ow.writeObject(decodingTrie);
        int numOfSymbols = file.length;
        ow.writeObject(numOfSymbols);
        Map<Character, BitSequence> lookupTable = decodingTrie.buildLookupTable();
        List<BitSequence> bitSequenceList = new ArrayList<>();
        for (char c: file) {
            bitSequenceList.add(lookupTable.get(c));
        }
        BitSequence hugeBitsequence = BitSequence.assemble(bitSequenceList);
        ow.writeObject(hugeBitsequence);
    }
}
