package byog.Core;

import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.File;
import java.util.regex.*;


public class Game {
    static long SUBSEED;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
       try {
           MapGenerator.drawFrame();
           while (true) {
               if (!StdDraw.hasNextKeyTyped()) {
                   continue;
               }
               char temp = StdDraw.nextKeyTyped();
               switch (temp) {
                   case 'n': MapGenerator.drawText("Please enter a seed and press 'S' to start", 35);
                        StringBuilder input = new StringBuilder();
                        while (temp != 's') {
                           if (!StdDraw.hasNextKeyTyped()) {
                               continue;
                           }
                           temp = StdDraw.nextKeyTyped();
                           input.append(temp);
                        }
                        String s = input.substring(0, input.length() - 1);
                        SUBSEED = Long.parseLong(s);
                        MapGenerator.run();
                        break;
                   case 'q': System.exit(0);
                        break;
                   case 'l': File f = new File("./game.ser");
                       if ((!f.exists())) {
                       MapGenerator.drawRedText("No previous game has been saved.", 30);
                       return;
                   }
                        StdDraw.enableDoubleBuffering();
                        MapGenerator.run(MapGenerator.loadWorld());
               }
           }
       } catch (NumberFormatException e) {
           MapGenerator.drawRedText("Sorry, you didn't enter the proper number", 30);
       }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        String pattern1 = "(n)(\\d+)(s)([a-z]*)(:q)?";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(input);
        if (m1.find()) {
            if (m1.group(4) != null) {
                SUBSEED = Long.parseLong(m1.group(2));
                MapGenerator.runWithInputString(m1.group(4).toCharArray());
            }
            if (m1.group(5) != null) {
                MapGenerator.saveWorld(new Info(SUBSEED, MapGenerator.x, MapGenerator.y));
            }
            SUBSEED = Long.parseLong(m1.group(2));
            MapGenerator.runWithoutMove();
            return MapGenerator.world;
        }

        String pattern2 = "(l)([a-z]*)(:q)?";
        Pattern r2 = Pattern.compile(pattern2);
        Matcher m2 = r2.matcher(input);
        if (m2.find()) {
            if (m2.group(2) != null) {
                SUBSEED = MapGenerator.loadWorld().saveLastSeed;
                MapGenerator.runWithInputString(m2.group(2).toCharArray(), MapGenerator.loadWorld());
            }
            if (m2.group(3) != null) {
                MapGenerator.saveWorld(new Info(MapGenerator.loadWorld().saveLastSeed, MapGenerator.lastXX, MapGenerator.lastYY));
            }
            return MapGenerator.world;
        }
        return MapGenerator.world;
    }
}
