package steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static org.testng.AssertJUnit.*;

public class ZipCucumber {

    @Then("^gets the \"([^\"]*)\" entry in the spreadsheet saved in the memory$")
    public void there_is_a_entry_in_the_spreadsheet_saved_in_the_memory(String name) throws Throwable {
        ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(World.buffer));
        ZipEntry entry;

        while ((entry = stream.getNextEntry()) != null) {
            if (entry.getName().equals(name)) {
                World.in = stream;
                return;
            }
        }
        fail("Entry " + name + " not found");
    }

    @When("^gets the initial tag of the xml entry$")
    public void gets_the_initial_tag_of_the_xml_entry() throws Throwable {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(World.in);
        reader.nextTag();
        QName qName = reader.getName();
        World.name = qName.getPrefix() + ":" + qName.getLocalPart();
    }

    @Then("^the tag is \"([^\"]*)\"$")
    public void the_entry_is_a_valid_XML_file_starting_by_the_tag(String tag) throws Throwable {
        assertEquals(World.name, tag);
    }

    @When("^the file \"([^\"]*)\" is present in the manifest file$")
    public void the_file_is_present_in_the_manifest_file(String key) throws Throwable {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(World.in);

        boolean found = false;
        while (reader.hasNext())
        {
            reader.next();
            if (reader.isStartElement()) {
                String name = reader.getName().getPrefix() + ":" + reader.getName().getLocalPart();
                if (name.equals("manifest:file-entry") && reader.getAttributeValue(0).equals(key))
                    found = true;
            }
        }

        assertTrue(found);
    }

}
