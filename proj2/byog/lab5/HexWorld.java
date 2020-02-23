package byog.lab5;

import static byog.lab5.SingleHexagon.*;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    public static void main(String[] args) {
        prepare();
        tesselation(6);
        ter.renderFrame(world);
    }
}
