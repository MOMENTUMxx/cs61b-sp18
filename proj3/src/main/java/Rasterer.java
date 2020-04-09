import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private double lonDPP;
    private double resolution;
    private double maxResolution;
    private int depth;
    private int rowLeft;
    private int rowRight;
    private int columnUp;
    private int columnDown;
    private static final int SL = 288200;

    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * 给定文件名，计算对应的经纬度边界
     */
    private Map<String, Double> fileBounds(String file) {
        String pattern = "d([0-9])_x([0-9]+)_y([0-9]+)"; //匹配缩放等级、横纵坐标的正则表达式
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(file);
        int D = 0, xLevel = 0, yLevel = 0;
        if (m.find()) {
            D = Integer.parseInt(m.group(1));
            xLevel = Integer.parseInt(m.group(2));
            yLevel = Integer.parseInt(m.group(3));
        }

        double ullon = MapServer.ROOT_ULLON + (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)
                * (xLevel / Math.pow(2, D));
        double lrlon = MapServer.ROOT_ULLON + (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)
                * ((xLevel + 1) / Math.pow(2, D));
        double ullat = MapServer.ROOT_ULLAT - (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT)
                * (yLevel / Math.pow(2, D));
        double lrlat = MapServer.ROOT_ULLAT - (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT)
                * ((yLevel + 1) / Math.pow(2, D));

        Map<String, Double> toReturn = new HashMap<>();
        toReturn.put("ullon", ullon);
        toReturn.put("lrlon", lrlon);
        toReturn.put("ullat", ullat);
        toReturn.put("lrlat", lrlat);
        return toReturn;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
//        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        depth = 0;
        lonDPP = (params.get("lrlon") - params.get("ullon")) / params.get("w") * SL; //query box分辨率
        maxResolution = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / MapServer.TILE_SIZE * SL;
        resolution = maxResolution;

        while (resolution > lonDPP) {
            depth++;
            resolution = maxResolution / Math.pow(2, depth);
        }
        if (depth > 7) {
            depth = 7; //若缩放等级超出所提供的范围，则使用最高等级
        }

        double lonPerTile = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / Math.pow(2, depth);
        //计算一行中最左端Tile的坐标
        if (params.get("ullon") < MapServer.ROOT_ULLON) {
            rowLeft = 0;
        } else {
            rowLeft = (int) Math.floor((params.get("ullon") - MapServer.ROOT_ULLON) / lonPerTile);
        }
        //计算一行中最右端Tile的坐标
        rowRight = (int) (Math.pow(2, depth) - 1 - Math.floor((MapServer.ROOT_LRLON
                - params.get("lrlon")) / lonPerTile));
        if (params.get("lrlon") > MapServer.ROOT_LRLON) {
            rowRight = (int) (Math.pow(2, depth) - 1);
        }
        int columnNum = rowRight - rowLeft + 1; //列数

        double latPerTile = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2, depth);
        //计算一列中最上方Tile的坐标
        if (params.get("ullat") > MapServer.ROOT_ULLAT) {
            columnUp = 0;
        } else {
            columnUp = (int) Math.floor((MapServer.ROOT_ULLAT - params.get("ullat")) / latPerTile);
        }
        //计算一列中最下方Tile的坐标
        columnDown = (int) (Math.pow(2, depth) - 1 - Math.floor((params.get("lrlat")
                - MapServer.ROOT_LRLAT) / latPerTile));
        if (params.get("lrlat") < MapServer.ROOT_LRLAT) {
            columnDown = (int) (Math.pow(2, depth) - 1);
        }
        int rowNum = columnDown - columnUp + 1; //行数

        String[][] gridFiles = new String[rowNum][columnNum]; //包含文件名的二维数组
        for (int j = 0; j < rowNum; j++) {
            for (int i = 0; i < columnNum; i++) {
                gridFiles[j][i] = "d" + depth + "_x" + (rowLeft + i) + "_y"
                        + (columnUp + j) + ".png";
            }
        }

        //计算边界的经纬度
        double ullon = fileBounds("d" + depth + "_x" + rowLeft + "_y"
                + columnUp + ".png").get("ullon");
        double ullat = fileBounds("d" + depth + "_x" + rowLeft + "_y"
                + columnUp + ".png").get("ullat");
        double lrlon = fileBounds("d" + depth + "_x" + rowRight + "_y"
                + columnDown + ".png").get("lrlon");
        double lrlat = fileBounds("d" + depth + "_x" + rowRight + "_y"
                + columnDown + ".png").get("lrlat");

        results.put("depth", depth);
        results.put("render_grid", gridFiles);
        results.put("raster_ul_lon", ullon);
        results.put("raster_ul_lat", ullat);
        results.put("raster_lr_lon", lrlon);
        results.put("raster_lr_lat", lrlat);
        //异常情况时query_success设为false
        if (params.get("lrlon") < params.get("ullon") || params.get("ullat")
                < params.get("lrlat")) {
            results.put("query_success", false);
        } else {
            results.put("query_success", true);
        }

        return results;
    }

}
