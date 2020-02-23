package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class SingleHexagon {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 60;

    private static final long SEED = 520;
    private static final Random RANDOM = new Random(SEED);

    public static TERenderer ter = new TERenderer();
    public static TETile[][] world = new TETile[WIDTH][HEIGHT];

    /**
     * Rendering the whole window with NOTHING
     */
    public static void prepare() {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * A help method to draw a single row of a hexagon
     * @param side how many tiles in a hexagon's each side
     * @param seq the sequence number, start from 0
     * @param p Position is a class with fields X and Y, and no other methods
     */
    private static void drawASingleRow(int side, int seq, Position p, TETile t) {
        int blank;
        int num;
        if (seq < side) {
            blank = side - seq - 1;
            num = 2 * seq + side;
        } else {
            blank = seq - side;
            num = 5 * side - 2 * seq - 2;
        }
        for (int i = 0; i < blank; i++) {
            world[p.X + i][p.Y + seq] = Tileset.NOTHING;
        }
        for (int i = 0; i < num; i++) {
            world[p.X + blank + i][p.Y + seq] = t;
        }
    }

    /**
     * @param p p specifies the lower left corner of the hexagon
     */
    public static void drawAHexagon(int side, Position p, TETile t) {
        for (int i = 0; i < 2 * side; i++) {
            drawASingleRow(side, i, p, t);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.PLAYER;
            case 3: return Tileset.WATER;
            case 4: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * Draw a column consists of hexagons
     * @param xOffset the offset of column in x direction
     * @param yOffset the offset of column in y direction
     */
    public static void drawAColumn(int num, int side, int xOffset, int yOffset) {
        Position[] pos = new Position[num];
        for (int i = 0; i < num; i++) {
            pos[i] = new Position(xOffset, yOffset + 2 * i * side);
            drawAHexagon(side, pos[i], randomTile());
        }
    }

    /**
     * Drawing a tesselation of hexagons
     */
    public static void tesselation(int side) {
        if (side < 2) {
            throw new RuntimeException("the side of hexagon must bigger than 2");
        }
        int i = 2 * side - 1;
        // xBias decides the offset of tesselation in x direction
        int xBias = (WIDTH - 11 * side + 6) / 2;
        // yBias decides the offset of tesselation in y direction
        int yBias = (HEIGHT - 10 * side) / 2;
        drawAColumn(3, side, xBias + 4 * i, yBias + 2 * side);
        drawAColumn(4, side, xBias + 3 * i, yBias + side);
        drawAColumn(5, side, xBias + 2 * i, yBias);
        drawAColumn(4, side, xBias + i, yBias + side);
        drawAColumn(3, side, xBias, yBias + 2 * side);
    }

    public static void main(String[] args) {
        prepare();
        tesselation(5);
        ter.renderFrame(world);
    }
}
