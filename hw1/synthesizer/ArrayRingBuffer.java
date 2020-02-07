package synthesizer;
// package <package name>;
import java.util.Iterator;

public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        this.capacity = capacity;
        first = 0;
        last = 0;
        fillCount = 0;
        rb = (T[]) new Object[capacity];
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */
    public void enqueue(T x) {
        if (isFull()) {
            throw new RuntimeException("Ring buffer overflow");
        }
        rb[last] = x;
        last += 1;
        if (last == capacity) {
            last = 0;
        }
        fillCount += 1;
    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        T remove = rb[first];
        rb[first] = null;
        first += 1;
        if (first == capacity) {
            first = 0;
        }
        fillCount -= 1;
        return remove;
    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        T toReturn = rb[first];
        return toReturn;
    }

    @Override
    public Iterator<T> iterator() {
        return new KeyIterator();
    }

    private class KeyIterator implements Iterator<T> {
        private int ptr;
        KeyIterator() {
            ptr = first;
        }
        public boolean hasNext() {
            return (ptr != last);
        }
        public T next() {
            T returnItem = rb[ptr];
            ptr += 1;
            return returnItem;
        }
    }
}
