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
    public static String[] sort(String[] asciis) {
        int maxLength = Integer.MIN_VALUE;
        for (String ascii: asciis) {
            maxLength = Math.max(maxLength, ascii.length());
        }

        String[] strings = new String[asciis.length];
        for (int i = 0; i < asciis.length; i++) {
            String ascii = asciis[i];
            StringBuilder asciiBuilder = new StringBuilder(ascii);
            while (asciiBuilder.length() < maxLength) {
                asciiBuilder.append(" ");
            }
            ascii = asciiBuilder.toString();
            strings[i] = ascii;
        }

        String[] sorted = sortHelperLSD(strings, maxLength - 1);
        for (int i = maxLength - 2; i >= 0; i--) {
            sorted = sortHelperLSD(sorted, i);
        }
        return originalString(sorted);
    }

    private static String[] originalString(String[] sorted) {
        String[] toReturn = new String[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            String s = sorted[i].replaceAll(" ", "");
            toReturn[i] = s;
        }
        return toReturn;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static String[] sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort
        int max = Integer.MIN_VALUE;
        for (String s: asciis) {
            max = Math.max(max, s.charAt(index));
        }

        int[] counts = new int[max + 1];
        for (String ascii: asciis) {
            counts[ascii.charAt(index)]++;
        }

        int[] starts = new int[max + 1];
        int pos = 0;
        for (int i = 0; i < starts.length; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        String[] sorted = new String[asciis.length];
        for (String ascii : asciis) {
            int item = ascii.charAt(index);
            int place = starts[item];
            sorted[place] = ascii;
            starts[item] += 1;
        }
        return sorted;
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
