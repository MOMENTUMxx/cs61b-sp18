package byog.Core;

/**
 * Component类表示构成游戏世界的最小单元：矩形；
 * 规定每个矩形左下角的顶点为起点，对应坐标(xStart, yStart);
 * 同时每个矩形还有其长度length和宽度width*/
public class Component {
    int xStart;
    int yStart;
    int length;
    int width;

    public Component(int xStart, int yStart, int length, int width) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.length = length;
        this.width = width;
    }
}
