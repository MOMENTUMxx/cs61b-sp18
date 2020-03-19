package hw3.hash;

import java.util.HashMap;
import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        HashMap<Integer, Integer> hm = new HashMap<>();
        for (Oomage o: oomages) {
            int bucketNum = (o.hashCode() & 0x7FFFFFFF) % M;
            if (hm.containsKey(bucketNum)) {
                int value = hm.get(bucketNum);
                hm.put(bucketNum, ++value);
                continue;
            }
            hm.put(bucketNum, 1);
        }
        for (int key: hm.keySet()) {
            if (hm.get(key) < (oomages.size() / 50) || hm.get(key) > (oomages.size() / 2.5)) {
                return false;
            }
        }
        return true;
    }
}
