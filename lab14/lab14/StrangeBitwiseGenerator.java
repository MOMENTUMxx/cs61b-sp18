package lab14;

import lab14lib.Generator;

/**
 * Created by LujieWang on 2020/4/21.
 */
public class StrangeBitwiseGenerator implements Generator {
    private int period;
    private int state;

    public StrangeBitwiseGenerator(int period) {
        state = 0;
        this.period = period;
    }

    @Override
    public double next() {
        state = (state + 1);
        int weirdState = state & (state >> 3) & (state >> 8) % period;
        if (weirdState == period) {
            weirdState = 0;
        }
        return normalize(weirdState);
    }

    private double normalize(int x) {
        return (2.0 / (period - 1)) * x - 1;
    }
}
