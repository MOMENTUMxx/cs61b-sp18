import edu.princeton.cs.algs4.Picture;

/**
 * Created by LujieWang on 2020/4/21.
 */
public class SeamCarver {
    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    private void validate(Picture pic, int x, int y) {
        if (x < 0 || x >= pic.width() || y < 0 || y >= pic.height()) {
            throw new IndexOutOfBoundsException();
        }
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return energy(picture, x, y);
    }

    private double energy(Picture pic, int x, int y) {
        validate(pic, x, y);
        return quadraticSumOfX(pic, x, y) + quadraticSumOfY(pic, x, y);
    }

    private int leftOfX(Picture pic, int x) {
        //左边界向左为右边界
        if (x == 0) {
            return pic.width() - 1;
        }
        return x - 1;
    }

    private int rightOfX(Picture pic, int x) {
        //右边界向右为左边界
        if (x == pic.width() - 1) {
            return 0;
        }
        return x + 1;
    }

    private int upOfY(Picture pic, int y) {
        //上边界向上为下边界
        if (y == 0) {
            return pic.height() - 1;
        }
        return y - 1;
    }

    private int downOfY(Picture pic, int y) {
        //下边界向下为上边界
        if (y == pic.height() - 1) {
            return 0;
        }
        return y + 1;
    }

    private int rx(Picture pic, int x, int y) {
        return pic.get(rightOfX(pic, x), y).getRed() - pic.get(leftOfX(pic, x), y).getRed();
    }

    private int gx(Picture pic, int x, int y) {
        return pic.get(rightOfX(pic, x), y).getGreen() - pic.get(leftOfX(pic, x), y).getGreen();
    }

    private int bx(Picture pic, int x, int y) {
        return pic.get(rightOfX(pic, x), y).getBlue() - pic.get(leftOfX(pic, x), y).getBlue();
    }

    private double quadraticSumOfX(Picture pic, int x, int y) {
        return (Math.pow(rx(pic, x, y), 2) + Math.pow(gx(pic, x, y), 2)
                + Math.pow(bx(pic, x, y), 2));
    }

    private int ry(Picture pic, int x, int y) {
        return pic.get(x, downOfY(pic, y)).getRed() - pic.get(x, upOfY(pic, y)).getRed();
    }

    private int gy(Picture pic, int x, int y) {
        return pic.get(x, downOfY(pic, y)).getGreen() - pic.get(x, upOfY(pic, y)).getGreen();
    }

    private int by(Picture pic, int x, int y) {
        return pic.get(x, downOfY(pic, y)).getBlue() - pic.get(x, upOfY(pic, y)).getBlue();
    }

    private double quadraticSumOfY(Picture pic, int x, int y) {
        return (Math.pow(ry(pic, x, y), 2) + Math.pow(gy(pic, x, y), 2)
                + Math.pow(by(pic, x, y), 2));
    }

    //返回该图片的转置，避免寻找水平和垂直seam时的重复代码
    private Picture transposition(Picture pic) {
        Picture trans = new Picture(pic.height(), pic.width());

        for (int i = 0; i < pic.width(); i++) {
            for (int j = 0; j < pic.height(); j++) {
                trans.set(j, i, pic.get(i, j));
            }
        }

        return trans;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findVerticalSeam(transposition(picture));
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findVerticalSeam(picture);
    }

    private int[] findVerticalSeam(Picture pic) {
        double[][] energy = new double[pic.width()][pic.height()];
        double[][] cost = new double[pic.width()][pic.height()];
        int[][] prevPixel = new int[pic.width()][pic.height()]; //记录最短路径下每个pixel的前一个pixel
        int[] result = new int[pic.height()];

        //图像宽度仅为１时单独考虑
        if (pic.width() == 1) {
            for (int i = 0; i < pic.height(); i++) {
                result[i] = 0;
            }
            return result;
        }

        //计算energy的矩阵，避免每次重复计算
        for (int i = 0; i < pic.width(); i++) {
            for (int j = 0; j < pic.height(); j++) {
                energy[i][j] = energy(pic, i, j);
            }
        }

        //第一行最短路径就是energy
        for (int i = 0; i < pic.width(); i++) {
            cost[i][0] = energy[i][0];
        }

        for (int j = 1; j < pic.height(); j++) {
            for (int i = 0; i < pic.width(); i++) {
                if (i == 0) { //左边界
                    if (cost[i][j - 1] <= cost[i + 1][j - 1]) {
                        cost[i][j] = cost[i][j - 1] + energy[i][j];
                        prevPixel[i][j] = i;
                    } else {
                        cost[i][j] = cost[i + 1][j - 1] + energy[i][j];
                        prevPixel[i][j] = i + 1;
                    }
                } else if (i == pic.width() - 1) { //右边界
                    if (cost[i - 1][j - 1] <= cost[i][j - 1]) {
                        cost[i][j] = cost[i - 1][j - 1] + energy[i][j];
                        prevPixel[i][j] = i - 1;
                    } else {
                        cost[i][j] = cost[i][j - 1] + energy[i][j];
                        prevPixel[i][j] = i;
                    }
                } else {
                    if (cost[i][j - 1] <= cost[i + 1][j - 1]) {
                        if (cost[i - 1][j - 1] <= cost[i][j - 1]) {
                            cost[i][j] = cost[i - 1][j - 1] + energy[i][j];
                            prevPixel[i][j] = i - 1;
                        } else {
                            cost[i][j] = cost[i][j - 1] + energy[i][j];
                            prevPixel[i][j] = i;
                        }
                    } else {
                        if (cost[i - 1][j - 1] <= cost[i + 1][j - 1]) {
                            cost[i][j] = cost[i - 1][j - 1] + energy[i][j];
                            prevPixel[i][j] = i - 1;
                        } else {
                            cost[i][j] = cost[i + 1][j - 1] + energy[i][j];
                            prevPixel[i][j] = i + 1;
                        }
                    }
                }
            }
        }

        double minCost = cost[0][pic.height() - 1];
        int index = 0;
        for (int i = 0; i < pic.width(); i++) {
            if (cost[i][pic.height() - 1] < minCost) {
                minCost = cost[i][pic.height() - 1];
                index = i;
            }
        }

        result[pic.height() - 1] = index;

        for (int k = pic.height() - 1; k > 0; k--) {
            result[k - 1] = prevPixel[result[k]][k];
        }

        return result;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        picture = SeamRemover.removeHorizontalSeam(picture, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        picture = SeamRemover.removeVerticalSeam(picture, seam);
    }
}
