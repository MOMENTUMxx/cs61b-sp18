package hw2;

import edu.princeton.cs.introcs.StdStats;
import edu.princeton.cs.introcs.StdRandom;

public class PercolationStats {
    private double[] threshold;
    private int temp;
    private int T;
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        this.T = T;
        threshold = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation p = pf.make(N);
            while (!p.percolates()) {
                int x = StdRandom.uniform(N);
                int y = StdRandom.uniform(N);
                p.open(x, y);
            }
            threshold[temp++] = (p.numberOfOpenSites()) / (double) (N * N);
        }
    }

    public double mean() {
        return StdStats.mean(threshold);
    }

    public double stddev() {
        return StdStats.stddev(threshold);
    }

    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(T);
    }

    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(T);
    }

    public static void main(String[] args) {
        PercolationStats pp = new PercolationStats(2, 10000, new PercolationFactory());
        System.out.println(pp.mean());
        System.out.println(pp.stddev());
        System.out.println(pp.confidenceLow());
        System.out.println(pp.confidenceHigh());
    }
}
