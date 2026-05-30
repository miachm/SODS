package com.github.miachm.sods;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.AEADBadTagException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * ODF package encryption and decryption (AES-256-CBC and legacy Blowfish CFB).
 *
 * @see <a href="https://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part3.html">ODF Part 3</a>
 */
class OdfEncryption {

    static final String CHECKSUM_TYPE_SHA256_1K =
            "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0#sha256-1k";
    static final String ALGORITHM_AES256_CBC = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    static final String START_KEY_SHA256 = "http://www.w3.org/2000/09/xmldsig#sha256";
    private static final int GCM_TAG_BITS = 128;

    private static final int AES_ITERATIONS = 100000;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private OdfEncryption() {
    }

    static boolean shouldEncryptEntry(String path, String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (path == null || path.endsWith("/")) {
            return false;
        }
        return !"mimetype".equals(path) && !"META-INF/manifest.xml".equals(path);
    }

    static byte[] decrypt(byte[] encrypted, OdfEncryptionMetadata meta, String password)
            throws WrongPasswordException {
        if (password == null || password.isEmpty()) {
            throw new WrongPasswordException("Document is password protected");
        }
        validateSupportedLegacyEncryption(meta);
        if (meta.salt == null || meta.initialisationVector == null) {
            throw new WrongPasswordException("Encrypted entry is missing salt or IV in manifest");
        }
        try {
            byte[] salt = Base64.getDecoder().decode(meta.salt);
            byte[] iv = Base64.getDecoder().decode(meta.initialisationVector);
            boolean aes = meta.isAesCbc();
            byte[] startKey = digestPassword(password, aes);
            SecretKeySpec key = deriveKey(startKey, salt, meta.iterationCount, meta.derivedKeySize, aes);

            Cipher cipher = Cipher.getInstance(aes ? "AES/CBC/ISO10126Padding" : "Blowfish/CFB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] deflated = cipher.doFinal(encrypted);

            if (!checksumMatches(deflated, meta.checksum, aes)) {
                throw new WrongPasswordException("Incorrect password");
            }

            return inflate(deflated, meta.originalSize);
        } catch (WrongPasswordException e) {
            throw e;
        } catch (Exception e) {
            throw new WrongPasswordException("Incorrect password");
        }
    }

    static byte[] decryptEncryptedPackage(byte[] encryptedPackage, OdfEncryptionMetadata meta, String password)
            throws WrongPasswordException {
        if (password == null || password.isEmpty()) {
            throw new WrongPasswordException("Document is password protected");
        }
        validateSupportedPackageEncryption(meta);
        try {
            byte[] salt = Base64.getDecoder().decode(meta.salt);
            byte[] iv = Base64.getDecoder().decode(meta.initialisationVector);
            byte[] startKey = digestPassword(password, true);
            byte[] key = argon2id(startKey, salt, meta.argon2Iterations, meta.argon2Memory,
                    meta.argon2Lanes, meta.derivedKeySize);

            byte[] encrypted = encryptedPackage;
            if (startsWith(encryptedPackage, iv)) {
                encrypted = new byte[encryptedPackage.length - iv.length];
                System.arraycopy(encryptedPackage, iv.length, encrypted, 0, encrypted.length);
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] deflated = cipher.doFinal(encrypted);
            return inflate(deflated, meta.originalSize);
        } catch (AEADBadTagException e) {
            throw new WrongPasswordException("Incorrect password");
        } catch (WrongPasswordException e) {
            throw e;
        } catch (Exception e) {
            throw new WrongPasswordException("Incorrect password");
        }
    }

    static EncryptResult encrypt(byte[] plaintext, String password) {
        try {
            byte[] deflated = deflate(plaintext);
            boolean aes = true;
            byte[] startKey = digestPassword(password, aes);
            String checksum = checksum(deflated, aes);

            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);
            SecretKeySpec key = deriveKey(startKey, salt, AES_ITERATIONS, 32, aes);

            byte[] iv = new byte[16];
            SECURE_RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] ciphertext = cipher.doFinal(deflated, 0, deflated.length);

            OdfEncryptionMetadata.Builder metaBuilder = new OdfEncryptionMetadata.Builder();
            metaBuilder.originalSize = plaintext.length;
            metaBuilder.checksum = checksum;
            metaBuilder.checksumType = CHECKSUM_TYPE_SHA256_1K;
            metaBuilder.algorithmName = ALGORITHM_AES256_CBC;
            metaBuilder.initialisationVector = Base64.getEncoder().encodeToString(iv);
            metaBuilder.keyDerivationName = "PBKDF2";
            metaBuilder.salt = Base64.getEncoder().encodeToString(salt);
            metaBuilder.iterationCount = AES_ITERATIONS;
            metaBuilder.derivedKeySize = 32;
            metaBuilder.startKeyGenerationName = START_KEY_SHA256;
            metaBuilder.startKeySize = 32;
            OdfEncryptionMetadata metadata = metaBuilder.build();

            return new EncryptResult(ciphertext, metadata);
        } catch (Exception e) {
            throw new GenerateOdsException(e);
        }
    }

    static class EncryptResult {
        final byte[] ciphertext;
        final OdfEncryptionMetadata metadata;

        EncryptResult(byte[] ciphertext, OdfEncryptionMetadata metadata) {
            this.ciphertext = ciphertext;
            this.metadata = metadata;
        }
    }

    private static byte[] digestPassword(String password, boolean aes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(aes ? "SHA-256" : "SHA-1");
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] argon2id(byte[] startKey, byte[] salt, int iterations, int memory, int lanes, int keyBytes) {
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(iterations)
                .withMemoryAsKB(memory)
                .withParallelism(lanes);
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        byte[] result = new byte[keyBytes];
        generator.generateBytes(startKey, result);
        return result;
    }

    private static SecretKeySpec deriveKey(byte[] startKey, byte[] salt, int iterations, int keyBytes, boolean aes)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] dk = pbkdf2HmacSha1(startKey, salt, iterations, keyBytes);
        return new SecretKeySpec(dk, aes ? "AES" : "Blowfish");
    }

    // PBKDF2 (RFC 2898) with HMAC-SHA1, compatible with LibreOffice Blowfish packages.
    private static byte[] pbkdf2HmacSha1(byte[] password, byte[] salt, int iterationCount, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keyspec = new SecretKeySpec(password, "HmacSHA1");
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(keyspec);
        int hmacLen = hmac.getMacLength();
        int l = (keyLength % hmacLen > 0) ? (keyLength / hmacLen + 1) : (keyLength / hmacLen);
        int r = keyLength - (l - 1) * hmacLen;
        byte[] t = new byte[l * hmacLen];
        int offset = 0;
        for (int i = 1; i <= l; i++) {
            byte[] ur = new byte[hmacLen];
            byte[] ui = new byte[salt.length + 4];
            System.arraycopy(salt, 0, ui, 0, salt.length);
            ui[salt.length] = (byte) (i >>> 24);
            ui[salt.length + 1] = (byte) (i >>> 16);
            ui[salt.length + 2] = (byte) (i >>> 8);
            ui[salt.length + 3] = (byte) i;
            for (int j = 0; j < iterationCount; j++) {
                ui = hmac.doFinal(ui);
                for (int k = 0; k < hmacLen; k++) {
                    ur[k] ^= ui[k];
                }
            }
            System.arraycopy(ur, 0, t, offset, hmacLen);
            offset += hmacLen;
        }
        if (r < hmacLen) {
            byte[] dk = new byte[keyLength];
            System.arraycopy(t, 0, dk, 0, keyLength);
            return dk;
        }
        return t;
    }

    private static byte[] deflate(byte[] data) {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(data.length, 64));
        byte[] buffer = new byte[4096];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        deflater.end();
        return baos.toByteArray();
    }

    private static void validateSupportedLegacyEncryption(OdfEncryptionMetadata meta) {
        if (!meta.isPbkdf2()) {
            throw unsupported(meta, "key derivation function", meta.keyDerivationName);
        }
        if (!meta.isAesCbc() && !meta.isBlowfishCfb()) {
            throw unsupported(meta, "encryption algorithm", meta.algorithmName);
        }
    }

    private static void validateSupportedPackageEncryption(OdfEncryptionMetadata meta) {
        if (!meta.isArgon2id()) {
            throw unsupported(meta, "key derivation function", meta.keyDerivationName);
        }
        if (!meta.isAesGcm()) {
            throw unsupported(meta, "encryption algorithm", meta.algorithmName);
        }
        if (meta.derivedKeySize != 32) {
            throw unsupported(meta, "AES-GCM key size", String.valueOf(meta.derivedKeySize));
        }
    }

    private static OperationNotSupportedException unsupported(OdfEncryptionMetadata meta, String field, String value) {
        return new OperationNotSupportedException("Unsupported ODF encryption " + field + ": " + value
                + " (algorithm=" + meta.algorithmName + ", key-derivation=" + meta.keyDerivationName + ")");
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data == null || prefix == null || data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[] inflate(byte[] deflated, int originalSize) throws DataFormatException {
        Inflater inflater = new Inflater(true);
        inflater.setInput(deflated);
        if (originalSize > 0) {
            byte[] output = new byte[originalSize];
            int inflated = inflater.inflate(output);
            inflater.end();
            if (inflated != originalSize) {
                byte[] trimmed = new byte[inflated];
                System.arraycopy(output, 0, trimmed, 0, inflated);
                return trimmed;
            }
            return output;
        }
        // originalSize unknown (manifest size attribute absent); inflate into a growing buffer
        ByteArrayOutputStream baos = new ByteArrayOutputStream(deflated.length * 4);
        byte[] buf = new byte[4096];
        int n;
        while ((n = inflater.inflate(buf)) > 0) {
            baos.write(buf, 0, n);
        }
        inflater.end();
        return baos.toByteArray();
    }

    private static String checksum(byte[] deflated, boolean aes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(aes ? "SHA-256" : "SHA-1");
        int len = Math.min(deflated.length, 1024);
        md.update(deflated, 0, len);
        return Base64.getEncoder().encodeToString(md.digest());
    }

    private static boolean checksumMatches(byte[] deflated, String expected, boolean aes)
            throws NoSuchAlgorithmException {
        if (expected == null) {
            return true;  // checksum is optional in ODF 1.0/1.1; skip validation when absent
        }
        return checksum(deflated, aes).equals(expected);
    }
}
