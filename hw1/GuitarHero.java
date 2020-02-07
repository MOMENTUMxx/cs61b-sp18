/**
 * Created by LujieWang on 2020/2/6.
 */
import synthesizer.GuitarString;
public class GuitarHero {
    private static final double CONSTANT = 440.0;
    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        double[] concert = new double[37];
        for (int i = 0; i < concert.length; i++) {
            concert[i] = CONSTANT * Math.pow(2, (i - 24) / 12);
        }
        GuitarString[] string = new GuitarString[37];
        for (int i = 0; i < concert.length; i++) {
            string[i] = new GuitarString(concert[i]);
        }
        while (true) {
            try {
                if (StdDraw.hasNextKeyTyped()) {
                    char key = StdDraw.nextKeyTyped();
                    int num = keyboard.indexOf(key);
                    string[num].pluck();
                }
                double sample = 0;
                for (int i = 0; i < concert.length; i++) {
                    sample += string[i].sample();
                }
                StdAudio.play(sample);
                for (int i = 0; i < concert.length; i++) {
                    string[i].tic();
                }
            } catch (Exception e) {
                System.out.println("This key is not related");
            }
        }
    }


}
