package byog.Core;

import java.io.Serializable;

public class Info implements Serializable {
    private static final long serialVersionUID = 123123123456456L;

    public long saveLastSeed;
    public int lastPlayerX;
    public int lastPlayerY;

    public Info(long saveLastSeed, int lastPlayerX, int lastPlayerY) {
        this.lastPlayerX = lastPlayerX;
        this.lastPlayerY = lastPlayerY;
        this.saveLastSeed = saveLastSeed;
    }
}
