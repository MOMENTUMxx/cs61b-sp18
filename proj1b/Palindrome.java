/**
 * Created by LujieWang on 2020/1/6.
 */
public class Palindrome {

    public boolean  isPalindrome(String word) {
        if (word.length() == 0 || word.length() == 1) {
            return true;
        }
        Deque<Character> deque = wordToDeque(word);
        return isPalindrome(deque);
    }

    private boolean isPalindrome(Deque<Character> deque) {
        if (deque.removeFirst() != deque.removeLast()) {
            return false;
        } else {
            if (deque.size() == 0 || deque.size() == 1) {
                return true;
            }
            return isPalindrome(deque);
        }
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        int length = word.length();
        for (int i = 0; i < length / 2; i++) {
            if (!cc.equalChars(word.charAt(i), word.charAt(length - i - 1))) {
                return false;
            }
        }
        return true;
    }

    public Deque<Character> wordToDeque(String word) {
        Deque<Character> container = new LinkedListDeque<>();
        for (int i = 0; i < word.length(); i++) {
            container.addLast(word.charAt(i));
        }
        return container;
    }
}
