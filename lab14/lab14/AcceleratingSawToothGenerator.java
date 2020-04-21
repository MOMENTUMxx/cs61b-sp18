package lab14;

import lab14lib.Generator;

/**
 * Created by LujieWang on 2020/4/21.
 */
public class AcceleratingSawToothGenerator implements Generator {
    private int period;
    private double factor;
    private int state;

    public AcceleratingSawToothGenerator(int period, double factor) {
        state = 0;
        this.period = period;
        this.factor = factor;
    }

    @Override
    public double next() {
        state = (state + 1);
        if (state == period) {
            state = 0;
            period = (int) Math.round(period * factor);
        }
        return normalize(state);
    }

    private double normalize(int x) {
        return (2.0 / (period - 1)) * x - 1;
    }
}
