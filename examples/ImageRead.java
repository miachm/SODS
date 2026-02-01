import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SheetImage;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageRead {
    public static void main(String[] args) {
        try {
            SpreadSheet spread = new SpreadSheet(new File("resources/image.ods"));
            Sheet sheet = spread.getSheet(0);
            List<SheetImage> images = sheet.getImages();

            System.out.println("Images found: " + images.size());
            for (SheetImage image : images) {
                System.out.println("Path: " + image.getPath());
                System.out.println("Mime type: " + image.getMimeType());
                System.out.println("Position: x=" + image.getX() + ", y=" + image.getY());
                System.out.println("Size: " + image.getWidth() + " x " + image.getHeight());
                System.out.println("Anchor: row=" + image.getAnchorRow() + ", column=" + image.getAnchorColumn());
                System.out.println("Bytes: " + (image.getData() == null ? 0 : image.getData().length));
                System.out.println("---");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
