public class ArrayDeque<T> {

    public T[] items;
    public int nextFirst;
    public int nextLast;
    public int size;

    public ArrayDeque() {
        size = 0;
        items = (T[])new Object[8];
        nextFirst = 4;
        nextLast = 5;
    }

    public void addFirst(T item) {
        size++;
        items[nextFirst] = item;
        nextFirst--;
        if (nextFirst < 0)
            nextFirst = items.length;
    }

    public void addLast(T item) {
        size++;
        items[nextLast] = item;
        nextLast++;
        if (nextLast > items.length)
            nextLast = 0;
    }

    public boolean isEmpty() {
        return items.length == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (T item : items)
            System.out.println(item);
    }

    public T removeFirst() {
        size--;
        T first = get(nextFirst + 1);
        items[nextFirst + 1] = null;
        nextFirst++;
        return first;
    }

    public T removeLast() {
        size--;
        T last = get(nextLast - 1);
        items[nextLast - 1] = null;
        nextLast--;
        return last;
    }

    public T get(int index) {
        return items[index];
    }
}

