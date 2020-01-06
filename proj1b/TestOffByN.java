/**
 * Created by LujieWang on 2020/1/6.
 */
import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByN {

    OffByN offBy5 = new OffByN(5);

    @Test
    public void testEqualChars() {
        assertTrue(offBy5.equalChars('a', 'f'));
        assertTrue(offBy5.equalChars('f', 'a'));
        assertFalse(offBy5.equalChars('f', 'h'));
    }
}
