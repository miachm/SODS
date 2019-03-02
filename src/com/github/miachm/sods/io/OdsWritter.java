package com.github.miachm.sods.io;

import com.github.miachm.sods.spreadsheet.Range;
import com.github.miachm.sods.spreadsheet.Sheet;
import com.github.miachm.sods.spreadsheet.SpreadSheet;
import com.github.miachm.sods.spreadsheet.Style;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Internal class for generate ODS files.
 */
public class OdsWritter {
    private final static String office = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    private final static String table_namespace = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
    private final static String text_namespace = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    private final static String font_namespace = "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0";
    private final static String style_namespace = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";

    private SpreadSheet spread;
    private Compressor out;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private Map<Style, String> stylesUsed = new HashMap<>();
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

            final String namespace = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

            Element rootEle = dom.createElementNS(namespace, "manifest:manifest");
            rootEle.setAttributeNS(namespace, "manifest:version","1.2");

            e = dom.createElementNS(namespace, "manifest:file-entry");
            e.setAttributeNS(namespace, "manifest:full-path","/");
            e.setAttributeNS(namespace, "manifest:version","1.2");
            e.setAttributeNS(namespace, "manifest:media-type",MIMETYPE);
            rootEle.appendChild(e);

            e = dom.createElementNS(namespace, "manifest:file-entry");
            e.setAttributeNS(namespace, "manifest:full-path","content.xml");
            e.setAttributeNS(namespace, "manifest:media-type","text/xml");
            rootEle.appendChild(e);

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "no");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                ByteArrayOutputStream o = new ByteArrayOutputStream();
                tr.transform(new DOMSource(dom),
                        new StreamResult(o));

                o.close();
                out.addEntry(o.toByteArray(),"META-INF/manifest.xml");

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

            Element rootEle = dom.createElementNS(office,"office:document-content");
            rootEle.setAttributeNS(office,"office:version","1.2");

            rootEle.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:table",table_namespace);
            rootEle.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:text",text_namespace);
            rootEle.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:fo",font_namespace);
            rootEle.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:style", style_namespace);

            Element content = writeContent(dom);
            Element styles = writeStyles(dom);

            rootEle.appendChild(styles);
            rootEle.appendChild(content);
            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "no");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                ByteArrayOutputStream o = new ByteArrayOutputStream();
                tr.transform(new DOMSource(dom),
                        new StreamResult(o));

                o.close();
                out.addEntry(o.toByteArray(),"content.xml");

            } catch (TransformerException | IOException te) {
                System.err.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.err.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    private Element writeContent(Document dom)
    {
        Element spreadsheet = dom.createElement("office:spreadsheet");
        Element e = dom.createElement("office:body");
        e.appendChild(spreadsheet);
        for (Sheet sheet : spread.getSheets()) {
            Element table = dom.createElement("table:table");
            table.setAttribute("table:name",sheet.getName());
            for (int i = 0;i < sheet.getMaxColumns();i++){
                Element column = dom.createElementNS(table_namespace, "table:table-column");
                table.appendChild(column);
            }
            for (int i = 0;i < sheet.getMaxRows();i++){
                Element row = dom.createElement("table:table-row");

                Range r = sheet.getRange(i,0,1,sheet.getMaxColumns());

                for (int j = 0;j < sheet.getMaxColumns();j++) {
                    Range range = r.getCell(0,j);
                    Object v = range.getValue();
                    String formula = range.getFormula();
                    Style style = range.getStyle();

                    Element cell = dom.createElement("table:table-cell");

                    if (formula != null) {
                        cell.setAttribute("table:formula",formula);
                    }

                    if (v != null) {
                        String valueType = getValueType(v);
                        cell.setAttribute("office:value-type", valueType);
                        Element value = dom.createElement("text:p");
                        value.setTextContent("" + v);

                        if (!valueType.equals("string")) {
                            cell.setAttribute("office:value",""+v);
                        }
                        cell.appendChild(value);
                    }

                    if (!style.isDefault()) {
                        String key = stylesUsed.get(style);
                        if (key == null)
                        {
                            key = "cel" + stylesUsed.size();
                            stylesUsed.put(style, key);
                        }

                        cell.setAttribute("table:style-name", key);
                    }

                    row.appendChild(cell);
                }

                table.appendChild(row);
            }
            spreadsheet.appendChild(table);
        }

        return e;
    }

    private Element writeStyles(Document dom)
    {
        Element e = dom.createElement("office:automatic-styles");

        Iterator<Map.Entry<Style, String>> it = stylesUsed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Style, String> pair = it.next();
            Style style = pair.getKey();
            String name = pair.getValue();

            Element styleTable = dom.createElement("style:style");
            Element styleText = dom.createElement("style:text-properties");

            styleTable.setAttribute("style:family", "table-cell");
            styleTable.setAttribute("style:name", name);

            if (style.isItalic())
                styleText.setAttribute("fo:font-style", "italic");

            if (style.isBold())
                styleText.setAttribute("fo:font-weight", "bold");

            if (style.isUnderline()) {
                styleText.setAttribute("style:text-underline-style", "solid");
                styleText.setAttribute("style:text-underline-type", "single");
                styleText.setAttribute("style:text-underline-width", "auto");
                styleText.setAttribute("style:text-underline-color", "font-color");
            }

            if (style.getFontSize() != -1) {
                styleText.setAttribute("fo:font-size", "" + style.getFontSize() + "pt");
            }

            if (style.getFontColor() != null) {
                styleText.setAttribute("fo:color", style.getFontColor().toString());
            }

            if (style.getBackgroundColor() != null) {
                Element styleCell = dom.createElement("style:table-cell-properties");
                styleCell.setAttribute("fo:background-color", style.getBackgroundColor().toString());
                styleTable.appendChild(styleCell);
            }

            styleTable.appendChild(styleText);
            e.appendChild(styleTable);

        }
        return e;
    }

    private String getValueType(Object v) {
        if (v instanceof Integer || v instanceof Float || v instanceof Double) {
            return "float";
        }
        else {
            return "string";
        }
    }
}
