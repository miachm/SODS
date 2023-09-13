package com.github.miachm.sods;

import org.testng.SkipException;
import org.testng.annotations.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.File;

import static org.testng.AssertJUnit.*;

public class XMLInputFactoriesTest {

    // Disable this test by default, because XmlReaderEventImpl stores a static XMLInputFactory
    // instance, which is not reset after the test.
    @Test(enabled = false)
    public void testLoadUsingBundledXMLInputFactory() throws Exception {
        testUsingFactory("com.sun.xml.internal.stream.XMLInputFactoryImpl");
    }

    @Test
    public void testLoadUsingWoodstoxXMLInputFactory() throws Exception {
        testUsingFactory("com.ctc.wstx.stax.WstxInputFactory");
    }

    private void testUsingFactory(String factoryName) throws Exception {
        // Skip test if factory is not available
        try {
            Class.forName(factoryName);
        } catch (ClassNotFoundException e) {
            throw new SkipException(factoryName + " not found, ignore this test.");
        }

        try {
            System.setProperty(XMLInputFactory.class.getName(), factoryName);

            SpreadSheet spread = new SpreadSheet(new File("resources/metadataWithDtd.ods"));
            assertEquals(spread.getNumSheets(),1);

        } finally {
            System.clearProperty(XMLInputFactory.class.getName());
        }
    }

}
