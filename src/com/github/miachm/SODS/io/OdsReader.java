package com.github.miachm.SODS.io;

import com.github.miachm.SODS.exceptions.NotAnOds;
import com.github.miachm.SODS.exceptions.OperationNotSupported;
import com.github.miachm.SODS.spreadsheet.Range;
import com.github.miachm.SODS.spreadsheet.Sheet;
import com.github.miachm.SODS.spreadsheet.SpreadSheet;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final String MANIFEST_PATH = "META-INF/manifest.xml";
    private String main_path;
    private SpreadSheet spread;
    private Map<String,byte[]> files;

    private OdsReader(InputStream in,SpreadSheet spread) throws IOException {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */
        this.spread = spread;
        Uncompressor uncompressor = new Uncompressor(in);
        files = uncompressor.getFiles();

        checkMimeType(files);

        byte[] manifest = getManifest(files);
        readManifest(manifest);
        readContent();
    }

    static public void load(InputStream in,SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(in,spread);
    }

    private void checkMimeType(Map<String,byte[]> map){
        byte[] mimetype = map.get("mimetype");
        if (mimetype == null)
            throw new NotAnOds("This file doesn't contain a mimetype");

        String mimetype_string = new String(mimetype);
        if (!mimetype_string.equals(CORRECT_MIMETYPE))
            throw new NotAnOds("This file doesn't look like an ODS file. Mimetype: " + mimetype_string);
    }

    private byte[] getManifest(Map<String,byte[]> map){
        byte[] manifest = map.get(MANIFEST_PATH);
        if (manifest == null) {
            throw new NotAnOds("Error loading, it doesn't like an ODS file");
        }

        return manifest;
    }

    private void readManifest(byte[] manifest) {
        try{
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(manifest));

            Element root = doc.getDocumentElement();
            if (!root.getNodeName().equals("manifest:manifest")) {
                throw new NotAnOds("The signature of the manifest is not valid. Is it an ODS file?");
            }

            NodeList files = doc.getElementsByTagName("manifest:file-entry");
            iterateFilesEntryManifest(files);

        }catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private void iterateFilesEntryManifest(NodeList files){
        for (int i = 0;i < files.getLength();i++) {
            NamedNodeMap children = files.item(i).getAttributes();
            boolean main_path = false;
            String path = null;
            for (int j = 0;j <children.getLength();j++) {
                Node child = children.item(j);
                if (child.getNodeName().equals("manifest:encryption-data")) {
                    throw new OperationNotSupported("This file has encription technology that it's not supported" +
                            "by this library");
                }
                else if (child.getNodeName().equals("manifest:full-path")){
                    path = child.getNodeValue();
                }
                else if (child.getNodeName().equals("manifest:media-type")){
                    main_path = (child.getNodeValue().equals(CORRECT_MIMETYPE));
                }
            }

            if (main_path)
                this.main_path = path;
        }
    }

    private void readContent() {
        Set<String> names = files.keySet();

        for (String name : names){
            if (name.endsWith(".xml"))
                processContent(files.get(name));
        }
    }

    private void processContent(byte[] bytes) {
        try{
            if (bytes.length == 0)
                return;

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(bytes));

            Element root = doc.getDocumentElement();

            NodeList files = doc.getElementsByTagName("office:body");

            if (files != null) {
                Node n = files.item(0);
                if (n != null)
                    iterateFilesEntries(n.getChildNodes());
            }

        }catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private void iterateFilesEntries(NodeList files) {
        if (files == null)
            return;
        for (int i = 0;i < files.getLength();i++){
            Node node = files.item(i);
            if (node.getNodeName().equals("office:spreadsheet")){
                processSpreadsheet(node.getChildNodes());
            }
        }
    }

    private void processSpreadsheet(NodeList list) {
        for (int i = 0;i < list.getLength();i++){
            Node node = list.item(i);
            if (node.getNodeName().equals("table:table")){
                processTable(node);
            }
        }
    }

    private void processTable(Node node){
        NamedNodeMap atributes = node.getAttributes();
        String name = atributes.getNamedItem("table:name").getNodeValue();
        Sheet sheet = new Sheet(name);
        sheet.deleteRow(0);
        sheet.deleteColumn(0);

        NodeList new_list = node.getChildNodes();
        for (int i = 0;i < new_list.getLength();i++){
            Node n = new_list.item(i);
            if (n.getNodeName().equals("table:table-column")){
                sheet.appendColumns(getNumberOfColumns(n));
            }
            else if (n.getNodeName().equals("table:table-row")){
                sheet.appendRow();
                processCells(n.getChildNodes(),sheet);
            }
        }
        spread.appendSheet(sheet);
    }

    private int getNumberOfColumns(Node n){
        Node n5 = n.getAttributes().getNamedItem("table:number-columns-repeated");
        int incr = 1;
        if (n5 != null)
            incr = Integer.parseInt(n5.getNodeValue());
        return incr;
    }

    private void processCells(NodeList childNodes,Sheet sheet) {
        int column = 0;
        for (int i = 0;i < childNodes.getLength();i++){
            Node n = childNodes.item(i);
            if (n.getNodeName().equals("table:table-cell")){
                String valueType = getValueType(n);

                NodeList cells = n.getChildNodes();
                for (int j = 0;j < cells.getLength();j++) {
                    Node cell = cells.item(j);
                    if (cell.getNodeName().equals("text:p")) {
                        // TODO : Iterate over the children
                        Range range = sheet.getRange(sheet.getMaxRows() - 1, column);
                        range.setValue(getValue(cell.getTextContent(),valueType));
                    }
                }
                column++;
            }
        }
    }

    private String getValueType(Node n) {
        String valueType = "string";

        Node type = n.getAttributes().getNamedItem("office:value-type");
        if (type !=  null) {
            valueType = type.getNodeValue();
        }
        return valueType;
    }

    private Object getValue(String value, String valueType) {
        if (valueType.equals("integer")) {
            return Integer.parseInt(value);
        }
        else if (valueType.equals("float")) {
            return Double.parseDouble(value);
        }
        else {
            return value;
        }
    }

}
