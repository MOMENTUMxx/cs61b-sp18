/**
 * Created by LujieWang on 2020/1/6.
 */
public class OffByN implements CharacterComparator {

    private int N;

    public OffByN(int N) {
        this.N = N;
    }

    @Override
    public boolean equalChars(char x, char y) {
        return Math.abs((int) x - (int) y) == N;
    }
}
