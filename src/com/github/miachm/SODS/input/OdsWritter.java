package com.github.miachm.SODS.input;

import com.github.miachm.SODS.spreadsheet.SpreadSheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OdsWritter {

    private SpreadSheet spread;
    private Compressor out;

    private OdsWritter(OutputStream o, SpreadSheet spread) throws IOException {
        this.spread = spread;
        this.out = new Compressor(o);
    }

    public static void save(OutputStream out,SpreadSheet spread) throws IOException {
        new OdsWritter(out,spread).save();
    }

    private void save() throws IOException {
        writeManifest();
        writeSpreadsheet();
        out.close();
    }

    private void writeManifest() {
        Document dom;
        Element e = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element

            Element rootEle = dom.createElementNS("manifest","manifest");
            rootEle.setAttribute("version","1.2");

            // create data elements and place them under root
            e = dom.createElement("file-entry");
            e.setAttribute("full-path","/");
            e.setAttribute("version","1.2");
            e.setAttribute("media-type","application/vnd.oasis.opendocument.spreadsheet");
            rootEle.appendChild(e);

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                ByteArrayOutputStream o = new ByteArrayOutputStream();
                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(o));

                o.close();
                out.addEntry(o.toByteArray(),"./META-INF/manifest.xml");

            } catch (TransformerException te) {
                System.err.println(te.getMessage());
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.err.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    private void writeSpreadsheet() {
    }
}
