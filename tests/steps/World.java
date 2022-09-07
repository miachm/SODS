package steps;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.InputStream;
import java.util.List;

public class World {
    public static SpreadSheet spread;
    public static byte[] buffer;
    public static Sheet sheet;
    public static List<Sheet> list_sheets;
    public static Range range;
    public static InputStream in;
    public static String name;


    public static void reset() {
        spread = null;
        buffer = null;
        sheet = null;
        list_sheets = null;
        range = null;
        in = null;
        name = null;
    }

}
