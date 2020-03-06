package byog.Core;

import java.io.Serializable;

/**
 * Info类表示存档信息，实现了Serializable接口；
 * 游戏中有三个动态参数:saveLastSeed为上次生成世界的种子；
 * lastPlayerX和lastPlayerY为退出游戏时玩家所在的位置*/
public class Info implements Serializable {
    private static final long serialVersionUID = 123123123456456L;

    long saveLastSeed;
    int lastPlayerX;
    int lastPlayerY;

    public Info(long saveLastSeed, int lastPlayerX, int lastPlayerY) {
        this.lastPlayerX = lastPlayerX;
        this.lastPlayerY = lastPlayerY;
        this.saveLastSeed = saveLastSeed;
    }
}
