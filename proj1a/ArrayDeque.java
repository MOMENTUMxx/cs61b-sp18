/**
 * ArrayDeque
 * implemented in circular way
 * @author LujieWang
 */

public class ArrayDeque<T> {

    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int size;

    /**
     * Create an empty ArrayDeque.
     */
    public ArrayDeque() {
        size = 0;
        //Java doesn't allow to create generic array, so use cast.
        items = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
    }

    /**
     * Return true if deque is full, false otherwise.
     */
    private boolean isFull() {
        return size == items.length;
    }

    /**
     * Whether to downsize the deque.
     */
    private boolean isSparse() {
        return items.length >= 16 && size < (items.length / 4);
    }

    /**
     * Add one circularly
     */
    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    /**
     * Minus one circularly
     */
    private int minusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private void resize(int capacity) {
        T[] newDeque = (T[]) new Object[capacity];
        int oldIndex = plusOne(nextFirst);
        for (int newIndex = 0; newIndex < size; newIndex++) {
            newDeque[newIndex] = items[oldIndex];
            oldIndex = plusOne(oldIndex);
        }
        items = newDeque;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    private void upSize() {
        resize(size * 2);
    }

    private void downSize() {
        resize(items.length / 2);
    }

    public void addFirst(T item) {
        if (isFull()) {
            upSize();
        }
        size++;
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
    }

    public void addLast(T item) {
        if (isFull()) {
            upSize();
        }
        size++;
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = plusOne(nextFirst); i != nextLast; i++) {
            System.out.print(items[i] + " ");
            System.out.println();
        }
    }

    public T removeFirst() {
        if (isSparse()) {
            downSize();
        }
        if (size == 0) {
            return null;
        }
        size--;
        nextFirst = plusOne(nextFirst);
        T toRemove = items[nextFirst];
        items[nextFirst] = null;
        return toRemove;
    }

    public T removeLast() {
        if (isSparse()) {
            downSize();
        }
        if (size == 0) {
            return null;
        }
        size--;
        nextLast = minusOne(nextLast);
        T toRemove = items[nextLast];
        items[nextLast] = null;
        return toRemove;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int start = plusOne(nextFirst);
        return items[(start + index) % items.length];
    }
}

