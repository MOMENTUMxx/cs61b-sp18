package byog.Core;

//import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class MapGenerator {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    private static Random RANDOM;

//    private static TERenderer ter = new TERenderer();
    protected static TETile[][] world;

    private static Component[] collection;
    private static int size;

    /**
     * Rendering the whole window with NOTHING
     */
    public static void prepare() {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
//        ter.initialize(WIDTH, HEIGHT);

        long seed = Game.SUBSEED;
        RANDOM = new Random(seed);
        world = new TETile[WIDTH][HEIGHT];
        collection = new Component[1000];
        size = 0;

        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

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

    public static Component createRoom() {
        int xStart = RandomUtils.uniform(RANDOM, 25, 30);
        int yStart = RandomUtils.uniform(RANDOM, 10, 15);
        int length = RandomUtils.uniform(RANDOM, 4, 9);
        int width = RandomUtils.uniform(RANDOM, 4, 9);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawARec(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public static Component rHallwayNearRoom(Component c) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart + 1;
        int length = RandomUtils.uniform(RANDOM, 4, WIDTH / 8);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawRNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public static Component lHallwayNearRoom(Component c) {
        int length = RandomUtils.uniform(RANDOM, 4, WIDTH / 8);
        int width = 3;
        int xStart = c.xStart - length;
        int yStart = c.yStart + 1;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawLNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length + 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public static Component uHallwayNearRoom(Component c) {
        int xStart = c.xStart + 1;
        int yStart = c.yStart + c.width - 1;
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawUNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public static Component dHallwayNearRoom(Component c) {
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int xStart = c.xStart + 1;
        int yStart = c.yStart - width;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawDNeighborHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width + 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public static Component uHallwayNearRHallway(Component c) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart;
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawUHallwayNearRHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart][c.yStart + 1] = Tileset.PLAYER;
        }
    }

    public static Component dHallwayNearRHallway(Component c) {
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart - width + 3;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawDHallwayNearRHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart][c.yStart + c.width - 2] = Tileset.GRASS;
        }
    }

    public static Component uHallwayNearLHallway(Component c) {
        int xStart = c.xStart - 2;
        int yStart = c.yStart;
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawUHallwayNearLHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 2][c.yStart + 1] = Tileset.WATER;
        }
    }

    public static Component dHallwayNearLHallway(Component c) {
        int length = 3;
        int width = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int xStart = c.xStart - 2;
        int yStart = c.yStart - width + 3;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawDHallwayNearLHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 2][c.yStart + c.width - 2] = Tileset.FLOWER;
        }
    }

    public static Component lHallwayNearUHallway(Component c) {
        int length = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int width = 3;
        int xStart = c.xStart - length + 3;
        int yStart = c.yStart + c.width - 1;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawLHallwayNearUHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + c.length - 2][c.yStart] = Tileset.LOCKED_DOOR;
        }
    }

    public static Component rHallwayNearUHallway(Component c) {
        int xStart = c.xStart;
        int yStart = c.yStart + c.width - 1;
        int length = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawRHallwayNearUHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 1][c.yStart] = Tileset.UNLOCKED_DOOR;
        }
    }

    public static Component lHallwayNearDHallway(Component c) {
        int length = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int width = 3;
        int xStart = c.xStart - length + 3;
        int yStart = c.yStart - 2;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawLHallwayNearDHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + c.length - 2][c.yStart + 2] = Tileset.SAND;
        }
    }

    public static Component rHallwayNearDHallway(Component c) {
        int xStart = c.xStart;
        int yStart = c.yStart - 1;
        int length = RandomUtils.uniform(RANDOM, 4, HEIGHT / 4);
        int width = 3;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawRHallwayNearDHallway(Component c) {
        if (notOutOfBounds(c)) {
            drawOutline(c);

            for (int i = c.xStart + 1; i < c.xStart + c.length - 1; i++) {
                for (int j = c.yStart + 1; j < c.yStart + c.width - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            world[c.xStart + 1][c.yStart + 2] = Tileset.MOUNTAIN;
        }
    }

    public static Component rRoomNearHallway(Component c) {
        int xStart = c.xStart + c.length - 1;
        int yStart = c.yStart - 1;
        int length = RandomUtils.uniform(RANDOM, 4, 7);
        int width = RandomUtils.uniform(RANDOM, 4, 7);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawRNeighborRoom(Component hallway, Component room) {
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

    public static Component lRoomNearHallway(Component c) {
        int length = RandomUtils.uniform(RANDOM, 4, 7);
        int width = RandomUtils.uniform(RANDOM, 4, 7);
        int xStart = c.xStart - length + 1;
        int yStart = c.yStart - 1;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawLNeighborRoom(Component hallway, Component room) {
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

    public static Component uRoomNearHallway(Component c) {
        int xStart = c.xStart - 1;
        int yStart = c.yStart + c.width - 1;
        int length = RandomUtils.uniform(RANDOM, 4, 7);
        int width = RandomUtils.uniform(RANDOM, 4, 7);
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawUNeighborRoom(Component hallway, Component room) {
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

    public static Component dRoomNearHallway(Component c) {
        int length = RandomUtils.uniform(RANDOM, 4, 7);
        int width = RandomUtils.uniform(RANDOM, 4, 7);
        int xStart = c.xStart - 1;
        int yStart = c.yStart - width + 1;
        Component cc = new Component(xStart, yStart, length, width);
        collection[size] = cc;
        size++;
        return cc;
    }

    public static void drawDNeighborRoom(Component hallway, Component room) {
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

    public static boolean notOutOfBounds(Component c) {
        return (c.xStart >= 0 && c.xStart < WIDTH
                && c.xStart + c.length < WIDTH
                && c.yStart >= 0 && c.yStart < HEIGHT
                && c.yStart + c.width < HEIGHT);
    }

    public static boolean notIntersect(Component c1, Component c2) {
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

    public static boolean notIntersect(Component c) {
        for (int i = 0; i < size - 1; i++) {
            if (!notIntersect(collection[i], c)) {
                return false;
            }
        }
        return true;
    }

    public static void generateWorld(Component c) {
        Component rHallway = rHallwayNearRoom(c);
        if (notOutOfBounds(rHallway) && notIntersect(rHallway)) {
            drawRNeighborHallway(rHallway);
            int num1 = RANDOM.nextInt(3);
            switch (num1) {
                case 0: Component rRoom = rRoomNearHallway(rHallway);
                    if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                        drawRNeighborRoom(rHallway, rRoom);
                    } else {
                        return;
                    }
                    generateWorld(rRoom);
                    break;
                case 1: Component uHallway1 = uHallwayNearRHallway(rHallway);
                    if (notOutOfBounds(uHallway1) && notIntersect(uHallway1)) {
                        drawUHallwayNearRHallway(uHallway1);
                        Component uRoom = uRoomNearHallway(uHallway1);
                        if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                            drawUNeighborRoom(uHallway1, uRoom);
                        } else {
                            return;
                        }
                        generateWorld(uRoom);
                    }
                    break;
                case 2: Component dHallway1 = dHallwayNearRHallway(rHallway);
                    if (notOutOfBounds(dHallway1) && notIntersect(dHallway1)) {
                        drawDHallwayNearRHallway(dHallway1);
                        Component dRoom = dRoomNearHallway(dHallway1);
                        if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                            drawDNeighborRoom(dHallway1, dRoom);
                        } else {
                            return;
                        }
                        generateWorld(dRoom);
                    }
                    break;
                default: return;
            }
        }

        Component uHallway = uHallwayNearRoom(c);
        if (notOutOfBounds(uHallway) && notIntersect(uHallway)) {
            drawUNeighborHallway(uHallway);
            int num2 = RANDOM.nextInt(3);
            switch (num2) {
                case 0: Component uRoom = uRoomNearHallway(uHallway);
                    if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                        drawUNeighborRoom(uHallway, uRoom);
                    } else {
                        return;
                    }
                    generateWorld(uRoom);
                    break;
                case 1: Component lHallway1 = lHallwayNearUHallway(uHallway);
                    if (notOutOfBounds(lHallway1) && notIntersect(lHallway1)) {
                        drawLHallwayNearUHallway(lHallway1);
                        Component lRoom = lRoomNearHallway(lHallway1);
                        if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                            drawLNeighborRoom(lHallway1, lRoom);
                        } else {
                            return;
                        }
                        generateWorld(lRoom);
                    }
                    break;
                case 2: Component rHallway1 = rHallwayNearUHallway(uHallway);
                    if (notOutOfBounds(rHallway1) && notIntersect(rHallway1)) {
                        drawRHallwayNearUHallway(rHallway1);
                        Component rRoom = rRoomNearHallway(rHallway1);
                        if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                            drawRNeighborRoom(rHallway1, rRoom);
                        } else {
                            return;
                        }
                        generateWorld(rRoom);
                    }
                    break;
                default:
            }
        }
    }

    public static void generateWorld1(Component c) {
        Component lHallway = lHallwayNearRoom(c);
        if (notOutOfBounds(lHallway) && notIntersect(lHallway)) {
            drawLNeighborHallway(lHallway);
            int num3 = RANDOM.nextInt(3);
            switch (num3) {
                case 0: Component lRoom = lRoomNearHallway(lHallway);
                    if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                        drawLNeighborRoom(lHallway, lRoom);
                    } else {
                        return;
                    }
                    generateWorld(lRoom);
                    break;
                case 1: Component uHallway1 = uHallwayNearLHallway(lHallway);
                    if (notOutOfBounds(uHallway1) && notIntersect(uHallway1)) {
                        drawUHallwayNearLHallway(uHallway1);
                        Component uRoom = uRoomNearHallway(uHallway1);
                        if (notOutOfBounds(uRoom) && notIntersect(uRoom)) {
                            drawUNeighborRoom(uHallway1, uRoom);
                        } else {
                            return;
                        }
                        generateWorld(uRoom);
                    }
                    break;
                case 2: Component dHallway1 = dHallwayNearLHallway(lHallway);
                    if (notOutOfBounds(dHallway1) && notIntersect(dHallway1)) {
                        drawDHallwayNearLHallway(dHallway1);
                        Component dRoom = dRoomNearHallway(dHallway1);
                        if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                            drawDNeighborRoom(dHallway1, dRoom);
                        } else {
                            return;
                        }
                        generateWorld(dRoom);
                    }
                    break;
                default: return;
            }
        }

        Component dHallway = dHallwayNearRoom(c);
        if (notOutOfBounds(dHallway) && notIntersect(dHallway)) {
            drawDNeighborHallway(dHallway);
            int num4 = RANDOM.nextInt(3);
            switch (num4) {
                case 0: Component dRoom = dRoomNearHallway(dHallway);
                    if (notOutOfBounds(dRoom) && notIntersect(dRoom)) {
                        drawDNeighborRoom(dHallway, dRoom);
                    } else {
                        return;
                    }
                    generateWorld(dRoom);
                    break;
                case 1: Component lHallway1 = lHallwayNearDHallway(dHallway);
                    if (notOutOfBounds(lHallway1) && notIntersect(lHallway1)) {
                        drawLHallwayNearDHallway(lHallway1);
                        Component lRoom = lRoomNearHallway(lHallway1);
                        if (notOutOfBounds(lRoom) && notIntersect(lRoom)) {
                            drawLNeighborRoom(lHallway1, lRoom);
                        } else {
                            return;
                        }
                        generateWorld(lRoom);
                    }
                    break;
                case 2: Component rHallway1 = rHallwayNearDHallway(dHallway);
                    if (notOutOfBounds(rHallway1) && notIntersect(rHallway1)) {
                        drawRHallwayNearDHallway(rHallway1);
                        Component rRoom = rRoomNearHallway(rHallway1);
                        if (notOutOfBounds(rRoom) && notIntersect(rRoom)) {
                            drawRNeighborRoom(rHallway1, rRoom);
                        } else {
                            return;
                        }
                        generateWorld(rRoom);
                    }
                    break;
                default:
            }
        }
    }

    public static void run() {
        prepare();

        Component c = createRoom();

        drawARec(c);
        generateWorld(c);
        generateWorld1(c);
//        world[c.xStart][c.yStart] = Tileset.LOCKED_DOOR;

//        ter.renderFrame(world);
    }
}
