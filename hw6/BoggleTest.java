import java.util.List;

/**
 * Created by LujieWang on 2020/4/25.
 */
public class BoggleTest {
    public static void main(String[] args) {
        List<String> result1 = Boggle.solve(7, "exampleBoard.txt");
        System.out.println(result1);
        Boggle.dictPath = "trivial_words.txt";
        List<String> result2 = Boggle.solve(20, "exampleBoard2.txt");
        System.out.println(result2);
    }
}
