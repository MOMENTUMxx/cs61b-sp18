/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    private static int RADIX = 256;

    public static String[] sort(String[] asciis) {
        int strLength = 0;
        for (String str : asciis) {
            strLength = strLength < str.length() ? str.length() : strLength;
        }

        String[] sorted = asciis.clone();
        for (int i = strLength - 1; i >= 0; i -= 1) {
            sortHelperLSD(sorted, i);
        }
        return sorted;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort
        int[] count = new int[RADIX];
        for (String str : asciis) {
            if (str.length() < index + 1) {
                count[0] += 1;
            } else {
                int position = (int) str.charAt(index);
                count[position] += 1;
            }
        }

        int[] start = new int[RADIX];
        int position = 0;
        for (int i = 0; i < start.length; i += 1) {
            start[i] = position;
            position += count[i];
        }


        String[] arrayBackup = asciis.clone();
        for (String str : arrayBackup) {
            int item = 0;
            if (str.length() >= index + 1) {
                item = (int) str.charAt(index);
            }
            asciis[start[item]] = str;
            start[item] += 1;
        }
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }
}