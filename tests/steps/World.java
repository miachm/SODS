package steps;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.util.List;

public class World {
    public static SpreadSheet spread;
    public static byte[] buffer;
    public static Sheet sheet;
    public static List<Sheet> list_sheets;


    public static void reset() {
        spread = null;
        buffer = null;
        sheet = null;
        list_sheets = null;
    }

}
