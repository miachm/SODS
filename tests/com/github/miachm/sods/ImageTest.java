package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class ImageTest {

    @Test
    public void testReadImage() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/image.ods"));
        Sheet sheet = spread.getSheet(0);
        List<SheetImage> images = sheet.getImages();

        assertEquals(1, images.size());
        SheetImage image = images.get(0);
        assertEquals("Pictures/1000000100000274000002C68DCA097E.png", image.getPath());
        assertEquals("image/png", image.getMimeType());
        assertNotNull(image.getData());
        assertEquals(Integer.valueOf(1), image.getAnchorRow());
        assertEquals(Integer.valueOf(1), image.getAnchorColumn());
        assertEquals("10.18cm", image.getWidth());
        assertEquals("11.509cm", image.getHeight());
        assertEquals("2.203cm", image.getX());
        assertEquals("0.425cm", image.getY());

        spread.save(new File("othercosita.ods"));
    }
}
