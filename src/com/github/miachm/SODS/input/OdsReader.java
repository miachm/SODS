package com.github.miachm.SODS.com.github.miachm.SODS.input;

import com.github.miachm.SODS.com.github.miachm.SODS.exceptions.NotAnOds;
import com.github.miachm.SODS.com.github.miachm.SODS.exceptions.OperationNotSupported;
import com.github.miachm.SODS.com.github.miachm.SODS.spreadsheet.SpreadSheet;
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
    private Map<String,byte[]> files;

    private OdsReader(InputStream in,SpreadSheet spread) throws IOException {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */

        Uncompressor uncompressor = new Uncompressor(in);
        files = uncompressor.getFiles();

        checkMimeType(files);

        byte[] manifest = getManifest(files);
        readManifest(manifest);
        readContent();
    }

    static public void load(InputStream in,SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(in,spread);
        reader.fill(spread);
    }

    private void fill(SpreadSheet spread) {
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

        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                    System.out.println("Manifest media type: " + child.getNodeValue());

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
            if (sameFolder(name,main_path)){
                processContent(files.get(name));
            }
        }
    }

    private boolean sameFolder(String name, String main_path) {
        if (!name.startsWith(main_path))
            return false;

        return name.indexOf('/',main_path.length()+1) < 0;
    }

    private void processContent(byte[] bytes) {
    }
}
