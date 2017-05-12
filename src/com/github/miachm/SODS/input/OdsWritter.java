package com.github.miachm.SODS.input;

import com.github.miachm.SODS.spreadsheet.Range;
import com.github.miachm.SODS.spreadsheet.Sheet;
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
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private final String MIMETYPE= "application/vnd.oasis.opendocument.spreadsheet";

    private OdsWritter(OutputStream o, SpreadSheet spread) throws IOException {
        this.spread = spread;
        this.out = new Compressor(o);
        dbf.setNamespaceAware(true);
    }

    public static void save(OutputStream out,SpreadSheet spread) throws IOException {
        new OdsWritter(out,spread).save();
    }

    private void save() throws IOException {
        writeManifest();
        writeMymeType();
        writeSpreadsheet();
        out.close();
    }

    private void writeManifest() {
        Document dom;
        Element e = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEle = dom.createElementNS("manifest","manifest");
            rootEle.setAttributeNS("manifest","version","1.2");
            rootEle.setAttributeNS("xmlns","manifest","urn:oasis:names:tc:opendocument:xmlns:manifest:1.0");

            e = dom.createElementNS("manifest","file-entry");
            e.setAttributeNS("manifest","full-path","/");
            e.setAttributeNS("manifest","version","1.2");
            e.setAttributeNS("manifest","media-type",MIMETYPE);
            rootEle.appendChild(e);

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                ByteArrayOutputStream o = new ByteArrayOutputStream();
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

    private void writeMymeType() throws IOException {
        out.addEntry(MIMETYPE.getBytes(),"mimetype");
    }

    private void writeSpreadsheet() {
        Document dom;
        Element e = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element rootEle = dom.createElementNS("office","document-content");
            rootEle.setAttribute("version","1.2");
            e = dom.createElementNS("office","body");

            Element spreadsheet = dom.createElementNS("office","spreadsheet");
            e.appendChild(spreadsheet);
            rootEle.appendChild(e);
            dom.appendChild(rootEle);

            for (Sheet sheet : spread.getSheets()) {
                Element table = dom.createElementNS("table", "table");
                table.setAttributeNS("table","name",sheet.getName());
                for (int i = 0;i < sheet.getMaxColumns();i++){
                    Element column = dom.createElementNS("table", "column");
                    table.appendChild(column);
                }
                for (int i = 0;i < sheet.getMaxRows();i++){
                    Element row = dom.createElementNS("table", "row");

                    Range r = sheet.getRange(i,0,1,sheet.getMaxColumns());

                    for (int j = 0;j < sheet.getMaxColumns();j++) {
                        Object v = r.getCell(0,j).getValue();
                        Element cell = dom.createElementNS("table", "table-cell");
                        cell.setAttributeNS("calcext","value-type","string"); // TODO change to correct type

                        Element value = dom.createElementNS("text","p");
                        value.setTextContent(""+v);
                        cell.appendChild(value);
                        row.appendChild(cell);
                    }

                    table.appendChild(row);
                }
                spreadsheet.appendChild(table);
            }

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                ByteArrayOutputStream o = new ByteArrayOutputStream();
                tr.transform(new DOMSource(dom),
                        new StreamResult(o));

                o.close();
                out.addEntry(o.toByteArray(),"./content.xml");

            } catch (TransformerException te) {
                System.err.println(te.getMessage());
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.err.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }
}
