package lab14;

import lab14lib.Generator;

/**
 * Created by LujieWang on 2020/4/21.
 */
public class SawToothGenerator implements Generator {
    private int period;
    private int state;

    public SawToothGenerator(int period) {
        state = 0;
        this.period = period;
    }

    @Override
    public double next() {
        state = (state + 1);
        if (state == period) {
            state = 0;
        }
        return normalize(state);
    }

    private double normalize(int x) {
        return (2.0 / (period - 1)) * x - 1;
    }
}
