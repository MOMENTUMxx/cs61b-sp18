package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.Random;

/**
 * MapGenerator是实现整个游戏的核心，负责生成游戏世界、处理各种不同情境，但实现方式很“笨”
 */
public class MapGenerator {
    /*控制游戏窗口大小的参数*/
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    /*用于生成伪随机数的变量，与种子相对应*/
    private static Random random;
    private static Random random2;

    private static TERenderer ter = new TERenderer(); //渲染图形的引擎
    static TETile[][] world; //整个世界是由一个个TETile组成的二维数组

    private static Component[] collection; //用来记录所生成的矩形
    private static int size; //collection数组中元素的数量

    /*玩家player所在位置*/
    static int x = 0;
    static int y = 0;

    /*读取存档后玩家所在位置*/
    static int lastXX = 0;
    static int lastYY = 0;

    /*用于读取游戏存档的函数*/
    static Info loadWorld() {
        File f = new File("./game.txt"); //注意后缀名为.txt否则无法上传Github
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Info loadWorld = (Info) os.readObject();
                os.close();
                return loadWorld;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /* In the case no World has been saved yet, we return a new one. */
        return new Info(0, 0, 0);
    }

    /*用于存储游戏进程的函数*/
    static void saveWorld(Info w) {
        File f = new File("./game.txt"); //注意后缀名为.txt否则无法上传Github
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /*游戏开始前的主菜单*/
    static void drawFrame() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();

        int midWidth = WIDTH / 2;
        int midHeight = HEIGHT / 2;

        StdDraw.clear();
        StdDraw.clear(Color.black);

        //Draw the GUI

        Font bigFont = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, HEIGHT - 10, "CS61B: THE GAME");


        //Draw the actual text
        Font samallFont = new Font("Monaco", Font.ROMAN_BASELINE, 30);
        StdDraw.setFont(samallFont);
        StdDraw.text(midWidth, midHeight - 5, "New Game (N)");
        StdDraw.text(midWidth, midHeight - 8, "Load Game (L)");
        StdDraw.text(midWidth, midHeight - 11, "Quit (Q)");
        StdDraw.show();
    }

    /*在屏幕中间显示文字（白色）*/
    static void drawText(String s) {
        StdDraw.clear(Color.black);
        Font smallFont = new Font("Monaco", Font.BOLD, 35);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    /*在屏幕中间显示文字（黄色）*/
    static void drawYellowText(String s) {
        StdDraw.clear(Color.black);
        Font smallFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    /*在屏幕中间显示文字（红色）*/
    static void drawRedText(String s) {
        StdDraw.clear(Color.black);
        Font smallFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.red);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    private static void initializeWorld() {
        world = new TETile[WIDTH][HEIGHT];
        collection = new Component[1000];
        size = 0;

        // initialize tiles
        for (int p = 0; p < WIDTH; p += 1) {
            for (int q = 0; q < HEIGHT; q += 1) {
                world[p][q] = Tileset.NOTHING;
            }
        }
    }

    /**
     * 用键盘输入时初次运行游戏对世界的初始化，seed来自键盘输入
     */
    private static void prepare() {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        ter.initialize(WIDTH, HEIGHT);

        long seed = Game.SUBSEED;
        random = new Random(seed);
        initializeWorld();
    }

    /*用键盘作为输入读取存档时对游戏的初始化，此时seed来自存档中的记录*/
    private static void prepare(long ss) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        ter.initialize(WIDTH, HEIGHT);

        random2 = new Random(ss);
        initializeWorld();
    }

    /*用字符串初次游戏时对世界的初始化，seed来自输入字符串*/
    private static void prepareWithInputString() {
        long seed = Game.SUBSEED;
        random = new Random(seed);
        initializeWorld();
    }

    /*用字符串读取存档时对世界的初始化，此时seed来自存档的记录*/
    private static void prepareWithInputString(long ss) {
        random2 = new Random(ss);
        initializeWorld();
    }

    /*绘制给定的Component的墙壁*/
    private static void drawOutline(Component c) {
        if (notOutOfBounds(c)) {
            for (int i = 0; i < c.length; i++) {
                world[c.xStart + i][c.yStart] = Tileset.WALL;
                world[c.xStart + i][c.yStart + c.width - 1] = Tileset.WALL;
            }

            for (int i = 0; i < c.width; i++) {
                world[c.xStart][c.yStart + i] = Tileset.WALL;
                world[c.xStart + c.length - 1][c.yStart + i] = Tileset.WALL;
            }
        }
    }

    /*创建世界中的第一个Component,位置位于屏幕中间的随机处*/
    private static Component createRoom(Random r) {
//        int xStart = RandomUtils.uniform(r, 25, 30);
//        int yStart = RandomUtils.uniform(r, 10, 15);
        int xStart = 0;
        int yStart = 0;
        int length = RandomUtils.uniform(r, 4, 9);
        int width = RandomUtils.uniform(r, 4, 9);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    /*绘制一个完整的Component（包括墙壁和地板）*/
    private static void drawARec(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * 返回一个房间右侧的走廊（地板长度或宽度为1的矩形叫走廊）
     * @param c 房间
     * @param r 用来做伪随机的Random实例
     */
    private static Component rHallwayNearRoom(Component c, Random r) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart + 1;
        int length = RandomUtils.uniform(r, 4, WIDTH / 8);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    /**
     * 绘制房间右侧的走廊
     * @param c 走廊
     */
    private static void drawRNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * 返回一个房间左侧的走廊（地板长度或宽度为1的矩形叫走廊）
     * @param c 房间
     * @param r 用来做伪随机的Random实例
     */
    private static Component lHallwayNearRoom(Component c, Random r) {
        int length = RandomUtils.uniform(r, 4, WIDTH / 8);
        int width = 3;
        int xStart = c.xStart - length;
        int yStart = c.yStart + 1;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    /**
     * 绘制房间左侧的走廊
     * @param c 走廊
     */
    private static void drawLNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length + 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * 返回一个房间顶侧的走廊（地板长度或宽度为1的矩形叫走廊）
     * @param c 房间
     * @param r 用来做伪随机的Random实例
     */
    private static Component uHallwayNearRoom(Component c, Random r) {
        int xStart = c.xStart + 1;
        int yStart = c.yStart + c.width - 1;
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    /**
     * 绘制房间顶侧的走廊
     * @param c 走廊
     */
    private static void drawUNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * 返回一个房间底侧的走廊（地板长度或宽度为1的矩形叫走廊）
     * @param c 房间
     * @param r 用来做伪随机的Random实例
     */
    private static Component dHallwayNearRoom(Component c, Random r) {
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int xStart = c.xStart + 1;
        int yStart = c.yStart - width;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    /**
     * 绘制房间底侧的走廊
     * @param c 走廊
     */
    private static void drawDNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width + 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    private static Component uHallwayNearRHallway(Component c, Random r) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart;
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawUHallwayNearRHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart][c.yStart + 1] = Tileset.FLOOR;
        }
    }

    private static Component dHallwayNearRHallway(Component c, Random r) {
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart - width + 3;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawDHallwayNearRHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart][c.yStart + c.width - 2] = Tileset.FLOOR;
        }
    }

    private static Component uHallwayNearLHallway(Component c, Random r) {
        int xStart = c.xStart - 2;
        int yStart = c.yStart;
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawUHallwayNearLHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 2][c.yStart + 1] = Tileset.FLOOR;
        }
    }

    private static Component dHallwayNearLHallway(Component c, Random r) {
        int length = 3;
        int width = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int xStart = c.xStart - 2;
        int yStart = c.yStart - width + 3;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawDHallwayNearLHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 2][c.yStart + c.width - 2] = Tileset.FLOOR;
        }
    }

    private static Component lHallwayNearUHallway(Component c, Random r) {
        int length = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int width = 3;
        int xStart = c.xStart - length + 3;
        int yStart = c.yStart + c.width - 1;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawLHallwayNearUHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + c.length - 2][c.yStart] = Tileset.FLOOR;
        }
    }

    private static Component rHallwayNearUHallway(Component c, Random r) {
        int xStart = c.xStart;
        int yStart = c.yStart + c.width - 1;
        int length = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawRHallwayNearUHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 1][c.yStart] = Tileset.FLOOR;
        }
    }

    private static Component lHallwayNearDHallway(Component c, Random r) {
        int length = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int width = 3;
        int xStart = c.xStart - length + 3;
        int yStart = c.yStart - 2;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawLHallwayNearDHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + c.length - 2][c.yStart + 2] = Tileset.FLOOR;
        }
    }

    private static Component rHallwayNearDHallway(Component c, Random r) {
        int xStart = c.xStart;
        int yStart = c.yStart - 1;
        int length = RandomUtils.uniform(r, 4, HEIGHT / 4);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawRHallwayNearDHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 1][c.yStart + 2] = Tileset.FLOOR;
        }
    }

    private static Component rRoomNearHallway(Component c, Random r) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart - 1;
        int length = RandomUtils.uniform(r, 4, 7);
        int width = RandomUtils.uniform(r, 4, 7);
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawRNeighborRoom(Component hallway, Component room) {
        if (notOutOfBounds(hallway) && notOutOfBounds(room)) {
            drawOutline(room);

            for (int i = room.xStart + 1; i < room.xStart + room.length - 1; i++) {
                for (int j = room.yStart + 1; j < room.yStart + room.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[hallway.xStart + hallway.length - 1][hallway.yStart + 1] = Tileset.FLOOR;
        }
    }

    private static Component lRoomNearHallway(Component c, Random r) {
        int length = RandomUtils.uniform(r, 4, 7);
        int width = RandomUtils.uniform(r, 4, 7);
        int xStart = c.xStart - length + 1;
        int yStart = c.yStart - 1;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawLNeighborRoom(Component hallway, Component room) {
        if (notOutOfBounds(hallway) && notOutOfBounds(room)) {
            drawOutline(room);

            for (int i = room.xStart + 1; i < room.xStart + room.length - 1; i++) {
                for (int j = room.yStart + 1; j < room.yStart + room.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }

            world[hallway.xStart][hallway.yStart + 1] = Tileset.FLOOR;
        }
    }

    private static Component uRoomNearHallway(Component c, Random r) {
        int xStart = c.xStart - 1;
        int yStart = c.yStart + c.width - 1;
        int length = RandomUtils.uniform(r, 4, 7);
        int width = RandomUtils.uniform(r, 4, 7);
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawUNeighborRoom(Component hallway, Component room) {
        if (notOutOfBounds(hallway) && notOutOfBounds(room)) {
            drawOutline(room);

            for (int i = room.xStart + 1; i < room.xStart + room.length - 1; i++) {
                for (int j = room.yStart + 1; j < room.yStart + room.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[hallway.xStart + 1][hallway.yStart + hallway.width - 1] = Tileset.FLOOR;
        }
    }

    private static Component dRoomNearHallway(Component c, Random r) {
        int length = RandomUtils.uniform(r, 4, 7);
        int width = RandomUtils.uniform(r, 4, 7);
        int xStart = c.xStart - 1;
        int yStart = c.yStart - width + 1;
        Component cc = new Component(xStart, yStart, length, width);
        if (notOutOfBounds(cc) && notIntersect(cc)) {
            collection[size] = cc;
            size++;
        }
        return cc;
    }

    private static void drawDNeighborRoom(Component hallway, Component room) {
        if (notOutOfBounds(hallway) && notOutOfBounds(room)) {
            drawOutline(room);

            for (int i = room.xStart + 1; i < room.xStart + room.length - 1; i++) {
                for (int j = room.yStart + 1; j < room.yStart + room.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }

            world[hallway.xStart + 1][hallway.yStart] = Tileset.FLOOR;
        }
    }

    /*判断Component是否超出屏幕范围*/
    private static boolean notOutOfBounds(Component c) {
        return (c.xStart >= 0 && c.xStart < WIDTH
                && c.xStart + c.length < WIDTH
                && c.yStart >= 0 && c.yStart < HEIGHT
                && c.yStart + c.width < HEIGHT);
    }

    /*判断两个Component是否有重叠*/
    private static boolean notIntersect(Component c1, Component c2) {
        int num = 0;
        for (int i = c1.xStart; i < c1.xStart + c1.length; i++) {
            for (int j = c2.xStart; j < c2.xStart + c2.length; j++) {
                for (int m = c1.yStart; m < c1.yStart + c1.width; m++) {
                    for (int n = c2.yStart; n < c2.yStart + c2.width; n++) {
                        if (i == j && m == n) {
                            num++;
                        }
                    }
                }
            }
        }
        return num <= 3;
    }

    /*判断该Component和其他所有Component是否有重叠*/
    private static boolean notIntersect(Component c) {
        for (int i = 0; i < size - 1; i++) {
            if (!notIntersect(collection[i], c)) {
                return false;
            }
        }
        return true;
    }

    /*以第一个创建的Component为中心生成右上部分的世界（递归）*/
    private static void generateWorldRU(Component c, Random r) {
        Component rHallway = rHallwayNearRoom(c, r);
        if (notOutOfBounds(rHallway) && notIntersect(rHallway)) {
            drawRNeighborHallway(rHallway);
            int num1 = r.nextInt(3);
            switch (num1) {
                case 0: Component rRoom = rRoomNearHallway(rHallway, r);
                    if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                        drawRNeighborRoom(rHallway, rRoom);
                    } else {
                        return;
                    }
                    generateWorldRU(rRoom, r);
                    break;
                case 1: Component uHallway1 = uHallwayNearRHallway(rHallway, r);
                    if (notOutOfBounds(uHallway1) && notIntersect(uHallway1)) {
                        drawUHallwayNearRHallway(uHallway1);
                        Component uRoom = uRoomNearHallway(uHallway1, r);
                        if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                            drawUNeighborRoom(uHallway1, uRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(uRoom, r);
                    }
                    break;
                case 2: Component dHallway1 = dHallwayNearRHallway(rHallway, r);
                    if (notOutOfBounds(dHallway1) && notIntersect(dHallway1)) {
                        drawDHallwayNearRHallway(dHallway1);
                        Component dRoom = dRoomNearHallway(dHallway1, r);
                        if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                            drawDNeighborRoom(dHallway1, dRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(dRoom, r);
                    }
                    break;
                default:
            }
        }

        Component uHallway = uHallwayNearRoom(c, r);
        if (notOutOfBounds(uHallway) && notIntersect(uHallway)) {
            drawUNeighborHallway(uHallway);
            int num2 = r.nextInt(3);
            switch (num2) {
                case 0: Component uRoom = uRoomNearHallway(uHallway, r);
                    if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                        drawUNeighborRoom(uHallway, uRoom);
                    } else {
                        return;
                    }
                    generateWorldRU(uRoom, r);
                    break;
                case 1: Component lHallway1 = lHallwayNearUHallway(uHallway, r);
                    if (notOutOfBounds(lHallway1) && notIntersect(lHallway1)) {
                        drawLHallwayNearUHallway(lHallway1);
                        Component lRoom = lRoomNearHallway(lHallway1, r);
                        if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                            drawLNeighborRoom(lHallway1, lRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(lRoom, r);
                    }
                    break;
                case 2: Component rHallway1 = rHallwayNearUHallway(uHallway, r);
                    if (notOutOfBounds(rHallway1) && notIntersect(rHallway1)) {
                        drawRHallwayNearUHallway(rHallway1);
                        Component rRoom = rRoomNearHallway(rHallway1, r);
                        if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                            drawRNeighborRoom(rHallway1, rRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(rRoom, r);
                    }
                    break;
                default:
            }
        }
    }

    /*以第一个创建的Component为中心生成左下部分的世界（递归）*/
    private static void generateWorldLD(Component c, Random r) {
        Component lHallway = lHallwayNearRoom(c, r);
        if (notOutOfBounds(lHallway) && notIntersect(lHallway)) {
            drawLNeighborHallway(lHallway);
            int num3 = r.nextInt(3);
            switch (num3) {
                case 0: Component lRoom = lRoomNearHallway(lHallway, r);
                    if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                        drawLNeighborRoom(lHallway, lRoom);
                    } else {
                        return;
                    }
                    generateWorldRU(lRoom, r);
                    break;
                case 1: Component uHallway1 = uHallwayNearLHallway(lHallway, r);
                    if (notOutOfBounds(uHallway1) && notIntersect(uHallway1)) {
                        drawUHallwayNearLHallway(uHallway1);
                        Component uRoom = uRoomNearHallway(uHallway1, r);
                        if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                            drawUNeighborRoom(uHallway1, uRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(uRoom, r);
                    }
                    break;
                case 2: Component dHallway1 = dHallwayNearLHallway(lHallway, r);
                    if (notOutOfBounds(dHallway1) && notIntersect(dHallway1)) {
                        drawDHallwayNearLHallway(dHallway1);
                        Component dRoom = dRoomNearHallway(dHallway1, r);
                        if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                            drawDNeighborRoom(dHallway1, dRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(dRoom, r);
                    }
                    break;
                default:
            }
        }

        Component dHallway = dHallwayNearRoom(c, r);
        if (notOutOfBounds(dHallway) && notIntersect(dHallway)) {
            drawDNeighborHallway(dHallway);
            int num4 = r.nextInt(3);
            switch (num4) {
                case 0: Component dRoom = dRoomNearHallway(dHallway, r);
                    if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                        drawDNeighborRoom(dHallway, dRoom);
                    } else {
                        return;
                    }
                    generateWorldRU(dRoom, r);
                    break;
                case 1: Component lHallway1 = lHallwayNearDHallway(dHallway, r);
                    if (notOutOfBounds(lHallway1) && notIntersect(lHallway1)) {
                        drawLHallwayNearDHallway(lHallway1);
                        Component lRoom = lRoomNearHallway(lHallway1, r);
                        if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                            drawLNeighborRoom(lHallway1, lRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(lRoom, r);
                    }
                    break;
                case 2: Component rHallway1 = rHallwayNearDHallway(dHallway, r);
                    if (notOutOfBounds(rHallway1) && notIntersect(rHallway1)) {
                        drawRHallwayNearDHallway(rHallway1);
                        Component rRoom = rRoomNearHallway(rHallway1, r);
                        if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                            drawRNeighborRoom(rHallway1, rRoom);
                        } else {
                            return;
                        }
                        generateWorldRU(rRoom, r);
                    }
                    break;
                default:
            }
        }
    }

    /*显示当前TIle的属性（墙壁、地板、玩家还是锁着的门）*/
    private static void drawStatus(String s) {
        Font smallFont = new Font("Monaco", Font.ROMAN_BASELINE, 18);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.textLeft(1, HEIGHT - 1, s);
        StdDraw.show();
    }

    /*在世界中标定玩家的位置*/
    private static void setPlayerLocation() {
        for (int i = WIDTH - 1; i > 0; i--) {
            int flag = 0;
            for (int j = HEIGHT - 1; j > 0; j--) {
                if (world[i][j] == Tileset.FLOOR) {
                    world[i][j] = Tileset.PLAYER;
                    ter.renderFrame(world);
                    x = i;
                    y = j;
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                break;
            }
        }
    }

    /*在世界中标定门的位置*/
    private static void setDoorLocation() {
        for (int i = 0; i < WIDTH; i++) {
            int flag = 0;
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] == Tileset.WALL) {
                    world[i][j + 1] = Tileset.LOCKED_DOOR;
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                break;
            }
        }
    }

    /*跟随鼠标移动显示Tile的信息*/
    private static void tileDescription() {
        int corX = (int) StdDraw.mouseX();
        int corY = (int) StdDraw.mouseY();
        if (corX == WIDTH) {
            corX -= 1;
        }
        if (corX == -1) {
            corX += 1;
        }
        if (corY == HEIGHT) {
            corY -= 1;
        }
        if (corY == -1) {
            corY += 1;
        }
        if (world[corX][corY] == Tileset.FLOOR) {
            drawStatus("floor");
            ter.renderFrame(world);

        }
        if (world[corX][corY] == Tileset.WALL) {
            drawStatus("wall");
            ter.renderFrame(world);
        }
        if (world[corX][corY] == Tileset.LOCKED_DOOR) {
            drawStatus("locked door");
            ter.renderFrame(world);
        }
        if (world[corX][corY] == Tileset.PLAYER) {
            drawStatus("player");
            ter.renderFrame(world);
        }
        if (world[corX][corY] == Tileset.NOTHING) {
            drawStatus("               ");
            ter.renderFrame(world);
        }
    }

    /*用键盘作为输入初次游戏时对玩家按键的响应*/
    private static void move() {
        setDoorLocation();
        setPlayerLocation();
        while (true) {
            tileDescription();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            switch (key) {
                case 'w':
                    if (!(world[x][y + 1] == Tileset.WALL)) {
                        if (world[x][y + 1] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[x][y] = Tileset.FLOOR;
                        y += 1;
                        world[x][y] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 's':
                    if (!(world[x][y - 1] == Tileset.WALL)) {
                        if (world[x][y - 1] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[x][y] = Tileset.FLOOR;
                        y -= 1;
                        world[x][y] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'a':
                    if (!(world[x - 1][y] == Tileset.WALL)) {
                        if (world[x - 1][y] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[x][y] = Tileset.FLOOR;
                        x -= 1;
                        world[x][y] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'd':
                    if (!(world[x + 1][y] == Tileset.WALL)) {
                        if (world[x + 1][y] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[x][y] = Tileset.FLOOR;
                        x += 1;
                        world[x][y] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'q': saveWorld(new Info(Game.SUBSEED, x, y));
                    System.exit(0);
                    break;
                default:
            }
        }
    }

    /*用键盘作为输入读取存档时对玩家按键的响应，此时玩家的位置来自存档的记录*/
    private static void move(int lastX, int lastY) {
        world[lastX][lastY] = Tileset.PLAYER;
        setDoorLocation();
        while (true) {
            tileDescription();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            switch (key) {
                case 'w':
                    if (!(world[lastX][lastY + 1] == Tileset.WALL)) {
                        if (world[lastX][lastY + 1] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastY += 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 's':
                    if (!(world[lastX][lastY - 1] == Tileset.WALL)) {
                        if (world[lastX][lastY - 1] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastY -= 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'a':
                    if (!(world[lastX - 1][lastY] == Tileset.WALL)) {
                        if (world[lastX - 1][lastY] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastX -= 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'd':
                    if (!(world[lastX + 1][lastY] == Tileset.WALL)) {
                        if (world[lastX + 1][lastY] == Tileset.LOCKED_DOOR) {
                            drawYellowText("YOU WIN !!!");
                            return;
                        }
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastX += 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        ter.renderFrame(world);
                    }
                    break;
                case 'q': saveWorld(new Info(loadWorld().saveLastSeed, lastX, lastY));
                    System.exit(0);
                    break;
                default:
            }
        }
    }

    /*用字符串作为输入初次游戏时对字符串中命令的响应*/
    private static void move(char[] c) {
        setPlayerLocation();
        setDoorLocation();
        for (char value : c) {
            switch (value) {
                case 'w':
                    if (!(world[x][y + 1] == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        y += 1;
                        world[x][y] = Tileset.PLAYER;
                    }
                    break;
                case 's':
                    if (!(world[x][y - 1] == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        y -= 1;
                        world[x][y] = Tileset.PLAYER;
                    }
                    break;
                case 'a':
                    if (!(world[x - 1][y] == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        x -= 1;
                        world[x][y] = Tileset.PLAYER;
                    }
                    break;
                case 'd':
                    if (!(world[x + 1][y] == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        x += 1;
                        world[x][y] = Tileset.PLAYER;
                    }
                    break;
                default:
            }
        }
    }

    /*用字符串作为输入读取存档时对字符串中命令的相应，此时玩家位置来自存档的记录*/
    private static void move(char[] c, int lastX, int lastY) {
        world[lastX][lastY] = Tileset.PLAYER;
        lastXX = lastX;
        lastYY = lastY;
        setDoorLocation();
        for (char value : c) {
            switch (value) {
                case 'w':
                    if (!(world[lastX][lastY + 1] == Tileset.WALL)) {
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastY += 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        lastXX = lastX;
                        lastYY = lastY;
                    }
                    break;
                case 's':
                    if (!(world[lastX][lastY - 1] == Tileset.WALL)) {
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastY -= 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        lastXX = lastX;
                        lastYY = lastY;
                    }
                    break;
                case 'a':
                    if (!(world[lastX - 1][lastY] == Tileset.WALL)) {
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastX -= 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        lastXX = lastX;
                        lastYY = lastY;
                    }
                    break;
                case 'd':
                    if (!(world[lastX + 1][lastY] == Tileset.WALL)) {
                        world[lastX][lastY] = Tileset.FLOOR;
                        lastX += 1;
                        world[lastX][lastY] = Tileset.PLAYER;
                        lastXX = lastX;
                        lastYY = lastY;
                    }
                    break;
                default:
            }
        }
    }

    /*用键盘玩游戏，初次运行*/
    public static void run() {
        prepare();

        Component c = createRoom(random);
        drawARec(c);
        generateWorldRU(c, random);
        generateWorldLD(c, random);

        ter.renderFrame(world);

        move();
    }

    /*用键盘玩游戏，读取存档*/
    public static void run(Info info) {
        prepare(info.saveLastSeed);

        Component c = createRoom(random2);
        drawARec(c);
        generateWorldRU(c, random2);
        generateWorldLD(c, random2);

        ter.renderFrame(world);

        move(info.lastPlayerX, info.lastPlayerY);
    }

    /*用字符串玩游戏，初次运行*/
    public static void runWithInputString(char[] chars) {
        prepareWithInputString();

        Component c = createRoom(random);
        drawARec(c);
        generateWorldRU(c, random);
        generateWorldLD(c, random);
        move(chars);
    }

    /*用字符串玩游戏，读取存档*/
    public static void runWithInputString(char[] chars, Info info) {
        prepareWithInputString(info.saveLastSeed);
        Component c = createRoom(random2);
        drawARec(c);
        generateWorldRU(c, random2);
        generateWorldLD(c, random2);
        move(chars, info.lastPlayerX, info.lastPlayerY);
    }

    /*用字符串玩游戏，但是只是生成世界，并没有其余命令*/
    public static void runWithoutMove() {
        prepareWithInputString();

        Component c = createRoom(random);
        drawARec(c);
        generateWorldRU(c, random);
        generateWorldRU(c, random);
        setPlayerLocation();
        setDoorLocation();
    }
}
