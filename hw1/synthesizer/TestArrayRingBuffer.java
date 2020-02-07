package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer(10);
        arb.enqueue(3);
        arb.enqueue(4);
        arb.enqueue(5);
        arb.enqueue(6);
        arb.enqueue(7);
        arb.enqueue(8);
        arb.enqueue(9);
        arb.enqueue(10);
        arb.enqueue(11);
        assertEquals(3, (long) arb.dequeue());
        assertEquals(4, (long) arb.dequeue());
        assertEquals(5, (long) arb.dequeue());
        assertEquals(6, (long) arb.dequeue());
        assertEquals(7, (long) arb.dequeue());
        arb.dequeue();
        assertEquals(3, arb.fillCount);
        assertEquals(9, (long) arb.peek());
        for (int i : arb) {
            System.out.println(i);
        }
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
