public class LinkedListDeque<T> {

    private class TNode {
        private T item;
        private TNode next;
        private TNode prev;

        public TNode(TNode p, T i, TNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private int size;
    private TNode sentinal;

    public LinkedListDeque() {
        size = 0;
        sentinal = new TNode(null, null, null);
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
    }
    public void addFirst(T item) {
        size++;
        sentinal.next = new TNode(sentinal, item, sentinal.next);
        sentinal.next.next.prev = sentinal.next;
    }

    public void addLast(T item) {
        size++;
        sentinal.prev = new TNode(sentinal.prev, item, sentinal);
        sentinal.prev.prev.next = sentinal.prev;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(sentinal.next.item + " ");
            sentinal = sentinal.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        size--;
        sentinal.next.next.prev = sentinal;
        sentinal.next = sentinal.next.next;
        return sentinal.next.item;
    }

    public T removeLast() {
        size--;
        sentinal.prev.prev.next = sentinal;
        sentinal.prev = sentinal.prev.prev;
        return sentinal.prev.item;
    }

    /** iteration*/
    public T get(int index) {
        TNode tmp = sentinal.next;
        for (int i = 0;i < index;i++) {
            tmp = tmp.next;
        }
        return tmp.item;
    }

/*
    public T getRecursive(int index) {
        if (index == 0)
            return sentinal.next.item;
        else {
            sentinal = sentinal.next;
            return getRecursive(index -1);
        }
    }*/

    /**recursion with a help method*/
    private T getRecursive(int index, TNode tmp) {
        if (index == 0) {
            return tmp.item;
        }
        else {
            return getRecursive(index - 1, tmp.next);
        }
    }

    public T getRecursive(int index) {
        return getRecursive(index, sentinal.next);
    }
}
