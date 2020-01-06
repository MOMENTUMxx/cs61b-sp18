/**
 * Created by LujieWang on 2020/1/6.
 */
public interface Deque<T> {

    void addFirst(T item);

    void addLast(T item);

    boolean isEmpty();

    int size();

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);
}
