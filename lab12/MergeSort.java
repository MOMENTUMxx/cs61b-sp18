import edu.princeton.cs.algs4.Queue;

public class MergeSort {
    /**
     * Removes and returns the smallest item that is in q1 or q2.
     *
     * The method assumes that both q1 and q2 are in sorted order, with the smallest item first. At
     * most one of q1 or q2 can be empty (but both cannot be empty).
     *
     * @param   q1  A Queue in sorted order from least to greatest.
     * @param   q2  A Queue in sorted order from least to greatest.
     * @return      The smallest item that is in q1 or q2.
     */
    private static <Item extends Comparable> Item getMin(
            Queue<Item> q1, Queue<Item> q2) {
        if (q1.isEmpty()) {
            return q2.dequeue();
        } else if (q2.isEmpty()) {
            return q1.dequeue();
        } else {
            // Peek at the minimum item in each queue (which will be at the front, since the
            // queues are sorted) to determine which is smaller.
            Comparable q1Min = q1.peek();
            Comparable q2Min = q2.peek();
            if (q1Min.compareTo(q2Min) <= 0) {
                // Make sure to call dequeue, so that the minimum item gets removed.
                return q1.dequeue();
            } else {
                return q2.dequeue();
            }
        }
    }

    /** Returns a queue of queues that each contain one item from items. */
    private static <Item extends Comparable> Queue<Queue<Item>>
            makeSingleItemQueues(Queue<Item> items) {
        Queue<Queue<Item>> toReturn = new Queue<>();
        for (Item i: items) {
            Queue<Item> q = new Queue<>();
            q.enqueue(i);
            toReturn.enqueue(q);
        }
        return toReturn;
    }

    /**
     * Returns a new queue that contains the items in q1 and q2 in sorted order.
     *
     * This method should take time linear in the total number of items in q1 and q2.  After
     * running this method, q1 and q2 will be empty, and all of their items will be in the
     * returned queue.
     *
     * @param   q1  A Queue in sorted order from least to greatest.
     * @param   q2  A Queue in sorted order from least to greatest.
     * @return      A Queue containing all of the q1 and q2 in sorted order, from least to
     *              greatest.
     *
     */
    private static <Item extends Comparable> Queue<Item> mergeSortedQueues(
            Queue<Item> q1, Queue<Item> q2) {
        Queue<Item> sortedQueue = new Queue<>();
        while (!(q1.isEmpty() && q2.isEmpty())) {
            sortedQueue.enqueue(getMin(q1, q2));
        }
        return sortedQueue;
    }

    /** Returns a Queue that contains the given items sorted from least to greatest. */
    public static <Item extends Comparable> Queue<Item> mergeSort(
            Queue<Item> items) {
        if (items.size() <= 1) {
            return items;
        }
        Queue<Queue<Item>> singleItemQueue = makeSingleItemQueues(items);
        int halfSize = items.size() / 2;
        Queue<Item> leftQueue = new Queue<>();
        for (int i = 0; i < halfSize; i++) {
            leftQueue.enqueue(singleItemQueue.dequeue().dequeue());
        }
        Queue<Item> rightQueue = new Queue<>();
        while (!singleItemQueue.isEmpty()) {
            rightQueue.enqueue(singleItemQueue.dequeue().dequeue());
        }
        Queue<Item> q1 = mergeSort(leftQueue);
        Queue<Item> q2 = mergeSort(rightQueue);
        return mergeSortedQueues(q1, q2);
    }

    public static void main(String[] args) {
        //对字母表进行排序（包含重复值）
        Queue<String> alphabet = new Queue<>();
        alphabet.enqueue("q");
        alphabet.enqueue("w");
        alphabet.enqueue("e");
        alphabet.enqueue("r");
        alphabet.enqueue("t");
        alphabet.enqueue("j");
        alphabet.enqueue("y");
        alphabet.enqueue("u");
        alphabet.enqueue("i");
        alphabet.enqueue("o");
        alphabet.enqueue("o");
        alphabet.enqueue("o");
        alphabet.enqueue("p");
        alphabet.enqueue("a");
        alphabet.enqueue("s");
        alphabet.enqueue("d");
        alphabet.enqueue("j");
        alphabet.enqueue("f");
        alphabet.enqueue("g");
        alphabet.enqueue("h");
        alphabet.enqueue("j");
        alphabet.enqueue("k");
        alphabet.enqueue("l");
        alphabet.enqueue("z");
        alphabet.enqueue("x");
        alphabet.enqueue("c");
        alphabet.enqueue("j");
        alphabet.enqueue("v");
        alphabet.enqueue("b");
        alphabet.enqueue("n");
        alphabet.enqueue("m");
        Queue<String> newAlphabet = mergeSort(alphabet);
        System.out.println(alphabet);
        System.out.println(newAlphabet);
    }
}
