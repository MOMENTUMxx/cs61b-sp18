package synthesizer;
import java.util.Iterator;
/**
 * Created by LujieWang on 2020/2/6.
 */
public interface BoundedQueue<T> extends Iterable<T> {
    int capacity();     // return size of the buffer
    int fillCount();    // return number of items currently in the buffer
    void enqueue(T x);  // add item to the end
    T dequeue();        // delete and return item from front
    T peek();           // return (but do not delete) item from the front

    default boolean isEmpty() {
        return fillCount() == 0;
    }

    default boolean isFull() {
        return capacity() == fillCount();
    }

    @Override
    Iterator<T> iterator();
}
