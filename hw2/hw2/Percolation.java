package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int N;
    private int openNum;
    private String[][] model;
    private WeightedQuickUnionUF wqu;
    private int TOP;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        TOP = N * N;
        this.N = N;
        openNum = 0;
        model = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                model[i][j] = "Blocked";
            }
        }
        wqu = new WeightedQuickUnionUF(N * N + 1);
    }

    private int xyTo1D(int r, int c) {
        return r * N + c;
    }

    private void validate(int row, int col) {
        if (!(row >= 0 && row <= N - 1 && col >= 0 && col <= N - 1)) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void open(int row, int col) {
        validate(row, col);
        if (!model[row][col].equals("Open")) {
            model[row][col] = "Open";
            openNum++;
        }
        if (row == 0) {
            wqu.union(TOP, xyTo1D(row, col));
        } else if (model[row - 1][col].equals("Open")) {
            wqu.union(xyTo1D(row, col), xyTo1D(row - 1, col));
        }
        if (row != N - 1) {
            if (model[row + 1][col].equals("Open")) {
                wqu.union(xyTo1D(row, col), xyTo1D(row + 1, col));
            }
        }
        if (col != 0) {
            if (model[row][col - 1].equals("Open")) {
                wqu.union(xyTo1D(row, col), xyTo1D(row, col - 1));
            }
        }
        if (col != N - 1) {
            if (model[row][col + 1].equals("Open")) {
                wqu.union(xyTo1D(row, col), xyTo1D(row, col + 1));
            }
        }
    }

    public boolean isOpen(int row, int col) {
        validate(row, col);
        return (model[row][col].equals("Open"));
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        return wqu.connected(TOP, xyTo1D(row, col));
    }

    public int numberOfOpenSites() {
        return openNum;
    }

    public boolean percolates() {
        for (int i = 0; i < N; i++) {
            if (wqu.connected(TOP, xyTo1D(N - 1, i))) {
                return true;
            }
        }
        return false;
    }
}
