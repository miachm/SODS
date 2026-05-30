package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DocumentEncryptionTest {

    @Test
    public void roundTripEncryptedSpreadsheet() throws Exception {
        Sheet sheet = new Sheet("Data", 2, 2);
        sheet.getRange(0, 0).setValue("secret");
        sheet.getRange(1, 1).setValue(42);

        SpreadSheet original = new SpreadSheet();
        original.appendSheet(sheet);
        original.setDocumentPassword("test-pass");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        original.save(buffer);

        assertManifestHasEncryption(buffer.toByteArray());

        OdsOptionParameters options = new OdsOptionParameters();
        options.setPassword("test-pass");
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(buffer.toByteArray()), options);

        assertEquals(loaded.getNumSheets(), 1);
        assertEquals(loaded.getSheet(0).getRange(0, 0).getValue(), "secret");
        assertEquals(loaded.getSheet(0).getRange(1, 1).getValue(), 42.0);
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void wrongPasswordThrows() throws Exception {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("S", 1, 1));
        spread.setDocumentPassword("right");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        spread.save(buffer);

        OdsOptionParameters options = new OdsOptionParameters();
        options.setPassword("wrong");
        new SpreadSheet(new ByteArrayInputStream(buffer.toByteArray()), options);
    }

    @Test(expectedExceptions = WrongPasswordException.class)
    public void missingPasswordThrows() throws Exception {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("S", 1, 1));
        spread.setDocumentPassword("secret");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        spread.save(buffer);

        new SpreadSheet(new ByteArrayInputStream(buffer.toByteArray()));
    }

    @Test(expectedExceptions = OperationNotSupportedException.class,
            expectedExceptionsMessageRegExp = ".*Unsupported ODF encryption.*")
    public void unsupportedEncryptionReportsUnsupportedInsteadOfWrongPassword() throws Exception {
        OdfEncryptionMetadata.Builder builder = new OdfEncryptionMetadata.Builder();
        builder.originalSize = 1;
        builder.algorithmName = "unsupported-algorithm";
        builder.keyDerivationName = "unsupported-kdf";
        builder.salt = "AAAA";
        builder.initialisationVector = "AAAA";

        Method decrypt = OdfEncryption.class.getDeclaredMethod(
                "decrypt", byte[].class, OdfEncryptionMetadata.class, String.class);
        decrypt.setAccessible(true);
        try {
            decrypt.invoke(null, new byte[0], builder.build(), "secret");
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    @Test
    public void loadLibreOfficeEncryptedFixtureIfPresent() throws Exception {
        File fixture = new File("encrypted.ods");
        if (!fixture.exists()) {
            return;
        }

        OdsOptionParameters options = new OdsOptionParameters();
        options.setPassword("cosita");
        SpreadSheet spread = new SpreadSheet(fixture, options);
        assertTrue(spread.getNumSheets() > 0);
    }

    private static void assertManifestHasEncryption(byte[] odsBytes) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(odsBytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("META-INF/manifest.xml".equals(entry.getName())) {
                    byte[] manifest = readAll(zip);
                    String text = new String(manifest, "UTF-8");
                    assertTrue(text.contains("encryption-data"), "manifest should describe encrypted entries");
                    return;
                }
            }
        }
        throw new AssertionError("manifest.xml not found");
    }

    private static byte[] readAll(java.io.InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) >= 0) {
            if (n > 0) out.write(buf, 0, n);
        }
        return out.toByteArray();
    }
}
