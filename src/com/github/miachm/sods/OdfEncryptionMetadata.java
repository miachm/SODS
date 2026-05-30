package com.github.miachm.sods;

/**
 * Encryption parameters for a single package file entry, as stored in {@code META-INF/manifest.xml}.
 */
class OdfEncryptionMetadata {
    final int originalSize;
    final String checksum;
    final String checksumType;
    final String algorithmName;
    final String initialisationVector;
    final String keyDerivationName;
    final String salt;
    final int iterationCount;
    final int derivedKeySize;
    final String startKeyGenerationName;
    final int startKeySize;
    final int argon2Iterations;
    final int argon2Memory;
    final int argon2Lanes;

    private OdfEncryptionMetadata(Builder b) {
        this.originalSize = b.originalSize;
        this.checksum = b.checksum;
        this.checksumType = b.checksumType;
        this.algorithmName = b.algorithmName;
        this.initialisationVector = b.initialisationVector;
        this.keyDerivationName = b.keyDerivationName;
        this.salt = b.salt;
        this.iterationCount = b.iterationCount;
        this.derivedKeySize = b.derivedKeySize;
        this.startKeyGenerationName = b.startKeyGenerationName;
        this.startKeySize = b.startKeySize;
        this.argon2Iterations = b.argon2Iterations;
        this.argon2Memory = b.argon2Memory;
        this.argon2Lanes = b.argon2Lanes;
    }

    boolean isAesCbc() {
        if (algorithmName == null) return false;
        String lower = algorithmName.toLowerCase();
        return lower.contains("aes256-cbc") || lower.contains("aes-256-cbc");
    }

    boolean isAesGcm() {
        if (algorithmName == null) return false;
        String lower = algorithmName.toLowerCase();
        return lower.contains("aes256-gcm") || lower.contains("aes-256-gcm");
    }

    boolean isBlowfishCfb() {
        if (algorithmName == null) return false;
        String lower = algorithmName.toLowerCase();
        return lower.contains("blowfish") && lower.contains("cfb");
    }

    boolean isArgon2id() {
        return keyDerivationName != null && keyDerivationName.toLowerCase().contains("argon2id");
    }

    boolean isPbkdf2() {
        if (keyDerivationName == null) return false;
        return "PBKDF2".equals(keyDerivationName)  || keyDerivationName.toLowerCase().contains("pbkdf2");
    }

    static class Builder {
        int originalSize = -1;
        String checksum;
        String checksumType;
        String algorithmName;
        String initialisationVector;
        String keyDerivationName;
        String salt;
        int iterationCount = 1024;
        int derivedKeySize = 16;
        String startKeyGenerationName;
        int startKeySize = 20;
        int argon2Iterations = 3;
        int argon2Memory = 65536;
        int argon2Lanes = 4;

        OdfEncryptionMetadata build() {
            return new OdfEncryptionMetadata(this);
        }
    }
}
