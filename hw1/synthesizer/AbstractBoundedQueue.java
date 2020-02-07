package synthesizer;

/**
 * Created by LujieWang on 2020/2/6.
 */
public abstract class AbstractBoundedQueue<T> implements BoundedQueue<T> {

    protected int fillCount;
    protected int capacity;
    public int capacity() {
        return capacity;
    }
    public int fillCount() {
        return fillCount;
    }
    public abstract T peek();
    public abstract T dequeue();
    public abstract void enqueue(T x);
}
