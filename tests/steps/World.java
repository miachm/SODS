package steps;

import com.github.miachm.sods.*;

import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLStreamReader;

public class World {
    public static SpreadSheet spread;
    public static byte[] buffer;
    public static Sheet sheet;
    public static List<Sheet> list_sheets;
    public static Range range;
    public static InputStream in;
    public static String name;
    public static ConditionalFormat conditionalFormat;
    public static Style style;
    public static ConditionalFormat otherConditionalFormat;
    public static XMLStreamReader tag;


    public static void reset() {
        spread = null;
        buffer = null;
        sheet = null;
        list_sheets = null;
        range = null;
        in = null;
        name = null;
        conditionalFormat = null;
        otherConditionalFormat = null;
        style = null;
        tag = null;
    }

}
