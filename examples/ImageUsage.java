import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SheetImage;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageUsage {
    public static void main(String[] args) {
        try {
            SpreadSheet spread = new SpreadSheet();
            Sheet sheet = new Sheet("Sheet1", 10, 8);
            spread.appendSheet(sheet);

            sheet.getRange(0, 0).setValue("Image demo");
            Range anchor = sheet.getRange(1, 1, 6, 4);

            byte[] imageBytes = Files.readAllBytes(new File("examples/duck.png").toPath());
            SheetImage image = sheet.addImage(anchor, imageBytes, "image/png");
            image.setName("Example image");

            spread.save(new File("image-example.ods"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
