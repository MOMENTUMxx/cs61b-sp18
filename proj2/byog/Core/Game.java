package byog.Core;

import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 这个类控制游戏启动的两种方式，若命令行参数不为空，则按参数提供的值运行游戏，
 * 否则使用键盘输入运行游戏。
 */
public class Game {
    static long SUBSEED; //使用伪随机所需要的种子

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     * 主菜单显示三个选项，分别是开始新游戏、退出和读取游戏。
     */
    public void playWithKeyboard() {
        try {
            MapGenerator.drawFrame(); //渲染主菜单
            while (true) {
                /*是否有键盘输入*/
                if (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                char temp = StdDraw.nextKeyTyped();
                switch (temp) {
                    case 'n': MapGenerator.drawText("Please enter a seed and press 'S'");
                        StringBuilder input = new StringBuilder(); //存放'N'之后输入的字符
                        /*获取n-s之间的数字作为种子*/
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
                    case 'l': File f = new File("./game.txt");
                        /*如果未经保存就读取游戏存档，则提示错误信息并终止游戏*/
                        if ((!f.exists())) {
                            MapGenerator.drawRedText("No previous game has been saved.");
                            return;
                        }
                        StdDraw.enableDoubleBuffering();
                        MapGenerator.run(MapGenerator.loadWorld()); //以读取的存档内容作为参数运行游戏
                        break;
                    default:
                }
            }
        } catch (NumberFormatException e) {
            /*如果输入的种子含有字母或其他非法字符则提示错误信息并终止游戏*/
            MapGenerator.drawRedText("Sorry, you didn't enter the proper number");
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
        /*匹配第一种可能的输入：以新游戏（N）开头，有seed、开始命令（S）、移动命令，可能有保存退出命令:q*/
        String pattern1 = "(n)(\\d+)(s)([a-z]+)(:q)?";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(input);
        if (m1.find()) {
            SUBSEED = Long.parseLong(m1.group(2)); //获取种子
            MapGenerator.runWithInputString(m1.group(4).toCharArray()); //传入移动命令
            /*如果匹配到:q则保存当前种子和玩家所在位置并退出*/
            if (m1.group(5) != null) {
                MapGenerator.saveWorld(new Info(SUBSEED, MapGenerator.x, MapGenerator.y));
            }
            return MapGenerator.world;
        }

        /*匹配第二种可能的输入：以读取存档（L）开始，可能有移动命令和保存退出命令:q*/
        String pattern2 = "(l)([a-z]*)(:q)?";
        Pattern r2 = Pattern.compile(pattern2);
        Matcher m2 = r2.matcher(input);
        if (m2.find()) {
            /*如果匹配到移动命令则将其作为参数运行游戏*/
            if (m2.group(2) != null) {
                MapGenerator.runWithInputString(m2.group(2).toCharArray(),
                        MapGenerator.loadWorld());
            }
            /*如果在读取存档的条件下再次保存，则种子不变依然为第一次保存时的种子，玩家位置更新为当前所在位置*/
            if (m2.group(3) != null) {
                MapGenerator.saveWorld(new Info(MapGenerator.loadWorld().saveLastSeed,
                        MapGenerator.lastXX, MapGenerator.lastYY)); //这几个参数的选取是关键
            }
            return MapGenerator.world;
        }

        /*匹配第三种可能的输入：以新游戏（N）开头，只有种子和开始命令（S），也就是说只需要生成游戏世界。
        * 尝试过与第一种匹配的情况合并，但会导致seed的值异常*/
        String pattern3 = "(n)(\\d+)(s)";
        Pattern r3 = Pattern.compile(pattern3);
        Matcher m3 = r3.matcher(input);
        if (m3.find()) {
            SUBSEED = Long.parseLong(m3.group(2));
            MapGenerator.runWithoutMove();
            return MapGenerator.world;
        }

        return null;
    }
}
