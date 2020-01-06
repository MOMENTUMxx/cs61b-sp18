import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        assertFalse("Not a palindrome.", palindrome.isPalindrome("cat"));
        assertTrue( palindrome.isPalindrome("noon"));
        assertTrue( palindrome.isPalindrome(""));
        assertTrue( palindrome.isPalindrome("3"));

        CharacterComparator offByOne = new OffByOne();
        assertFalse("Not a palindrome.", palindrome.isPalindrome("cat", offByOne));
        assertTrue( palindrome.isPalindrome("flake", offByOne));
    }
}
