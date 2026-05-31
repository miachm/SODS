package com.github.miachm.sods;

import java.util.Arrays;

/**
 * Pure-Java Argon2id key derivation (RFC 9106). No external dependencies.
 */
final class Kdf {

    private static final int BLOCK_SIZE = 128; // 64-bit words per block (= 1024 bytes)
    private static final int SYNC_POINTS = 4;

    private Kdf() {}

    static byte[] argon2id(byte[] password, byte[] salt, int t, int m, int p, int tagLen) {
        int segmentLen = Math.max(m / (SYNC_POINTS * p), 1);
        int laneLen = segmentLen * SYNC_POINTS;
        int totalBlocks = laneLen * p;

        byte[] h0 = computeH0(password, salt, t, m, p, tagLen);
        long[][] mem = new long[totalBlocks][BLOCK_SIZE];

        for (int lane = 0; lane < p; lane++) {
            initFirstBlock(mem, lane * laneLen,     h0, 0, lane);
            initFirstBlock(mem, lane * laneLen + 1, h0, 1, lane);
        }

        for (int pass = 0; pass < t; pass++) {
            for (int slice = 0; slice < SYNC_POINTS; slice++) {
                long[][] prs = null;
                if (isDataIndependent(pass, slice)) {
                    prs = new long[p][];
                    for (int lane = 0; lane < p; lane++) {
                        prs[lane] = computePseudoRands(pass, slice, lane, segmentLen, totalBlocks, t, p);
                    }
                }
                for (int lane = 0; lane < p; lane++) {
                    fillSegment(mem, pass, slice, lane, segmentLen, laneLen, p, totalBlocks,
                            prs != null ? prs[lane] : null);
                }
            }
        }

        long[] C = Arrays.copyOf(mem[laneLen - 1], BLOCK_SIZE);
        for (int lane = 1; lane < p; lane++) {
            long[] last = mem[lane * laneLen + laneLen - 1];
            for (int w = 0; w < BLOCK_SIZE; w++) C[w] ^= last[w];
        }
        return hPrime(blockToBytes(C), tagLen);
    }

    private static boolean isDataIndependent(int pass, int slice) {
        return pass == 0 && slice < SYNC_POINTS / 2;
    }

    private static byte[] computeH0(byte[] password, byte[] salt, int t, int m, int p, int tagLen) {
        Blake2b b = new Blake2b(64);
        b.update(le32(p));
        b.update(le32(tagLen));
        b.update(le32(m));
        b.update(le32(t));
        b.update(le32(19));  // version 0x13
        b.update(le32(2));   // Argon2id type
        b.update(le32(password.length));
        b.update(password);
        b.update(le32(salt.length));
        b.update(salt);
        b.update(le32(0));   // no secret
        b.update(le32(0));   // no associated data
        return b.digest();
    }

    private static void initFirstBlock(long[][] mem, int blockIdx, byte[] h0, int counter, int lane) {
        byte[] in = new byte[72];
        System.arraycopy(h0, 0, in, 0, 64);
        in[64] = (byte) counter;
        in[68] = (byte) lane;
        in[69] = (byte) (lane >>> 8);
        in[70] = (byte) (lane >>> 16);
        in[71] = (byte) (lane >>> 24);
        bytesToBlock(mem[blockIdx], hPrime(in, 1024));
    }

    private static long[] computePseudoRands(int pass, int slice, int lane,
                                              int segmentLen, int totalBlocks, int t, int p) {
        long[] prs = new long[segmentLen];
        long[] zero = new long[BLOCK_SIZE];
        long[] input = new long[BLOCK_SIZE];
        input[0] = pass;
        input[1] = lane;
        input[2] = slice;
        input[3] = totalBlocks;
        input[4] = t;
        input[5] = 2; // Argon2id
        long[] addr = new long[BLOCK_SIZE];
        int counter = 0;
        for (int i = 0; i < segmentLen; i++) {
            if (i % BLOCK_SIZE == 0) {
                input[6] = ++counter;
                fillBlock(zero, input, addr, false);
                fillBlock(zero, addr, addr, false);
            }
            prs[i] = addr[i % BLOCK_SIZE];
        }
        return prs;
    }

    private static void fillSegment(long[][] mem, int pass, int slice, int lane,
                                     int segmentLen, int laneLen, int p, int totalBlocks,
                                     long[] pseudoRands) {
        int startIdx = (pass == 0 && slice == 0) ? 2 : 0;
        int segStart = slice * segmentLen;

        for (int s = startIdx; s < segmentLen; s++) {
            int cur  = lane * laneLen + segStart + s;
            int prev = (cur == lane * laneLen) ? (lane * laneLen + laneLen - 1) : (cur - 1);

            long j1, j2;
            if (pseudoRands != null) {
                j1 = pseudoRands[s] & 0xFFFFFFFFL;
                j2 = (pseudoRands[s] >>> 32) & 0xFFFFFFFFL;
            } else {
                j1 = mem[prev][0] & 0xFFFFFFFFL;
                j2 = (mem[prev][0] >>> 32) & 0xFFFFFFFFL;
            }

            int refLane = (pass == 0 && slice == 0) ? lane : (int) (j2 % p);
            boolean sameLane = (refLane == lane);

            int refAreaSize;
            if (pass == 0) {
                if (slice == 0) {
                    refAreaSize = s - 1;
                } else if (sameLane) {
                    refAreaSize = segStart + s - 1;
                } else {
                    refAreaSize = segStart - (s == 0 ? 1 : 0);
                }
            } else {
                if (sameLane) {
                    refAreaSize = laneLen - segmentLen + s - 1;
                } else {
                    refAreaSize = laneLen - segmentLen - (s == 0 ? 1 : 0);
                }
            }
            if (refAreaSize < 0) refAreaSize = 0;

            long x = j1 * j1 >>> 32;
            long y = (long) refAreaSize * x >>> 32;
            int  z = (int) (refAreaSize - 1 - y);

            int startPos = (pass == 0) ? 0
                    : ((slice == SYNC_POINTS - 1) ? 0 : (slice + 1) * segmentLen);

            int refIdx = refLane * laneLen + (startPos + z) % laneLen;

            fillBlock(mem[prev], mem[refIdx], mem[cur], pass > 0);
        }
    }

    // Argon2 compression function (RFC 9106 §3.4)
    private static void fillBlock(long[] prev, long[] ref, long[] next, boolean withXor) {
        long[] R = new long[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) R[i] = prev[i] ^ ref[i];
        long[] tmp = Arrays.copyOf(R, BLOCK_SIZE);

        // BLAKE2_ROUND_NOMSG applied to 8 rows of 16 words
        for (int row = 0; row < 8; row++) {
            int o = row * 16;
            argon2G(R, o,   o+4, o+8,  o+12);
            argon2G(R, o+1, o+5, o+9,  o+13);
            argon2G(R, o+2, o+6, o+10, o+14);
            argon2G(R, o+3, o+7, o+11, o+15);
            argon2G(R, o,   o+5, o+10, o+15);
            argon2G(R, o+1, o+6, o+11, o+12);
            argon2G(R, o+2, o+7, o+8,  o+13);
            argon2G(R, o+3, o+4, o+9,  o+14);
        }

        // BLAKE2_ROUND_NOMSG applied to 8 columns (pairs spanning all 8 rows)
        for (int col = 0; col < 8; col++) {
            int o = col * 2;
            argon2G(R, o,    o+32, o+64,  o+96);
            argon2G(R, o+1,  o+33, o+65,  o+97);
            argon2G(R, o+16, o+48, o+80,  o+112);
            argon2G(R, o+17, o+49, o+81,  o+113);
            argon2G(R, o,    o+33, o+80,  o+113);
            argon2G(R, o+1,  o+48, o+81,  o+96);
            argon2G(R, o+16, o+49, o+64,  o+97);
            argon2G(R, o+17, o+32, o+65,  o+112);
        }

        for (int i = 0; i < BLOCK_SIZE; i++) {
            next[i] = tmp[i] ^ R[i] ^ (withXor ? next[i] : 0L);
        }
    }

    // Argon2's modified Blake2b G (with multiplication, no message words)
    private static void argon2G(long[] v, int a, int b, int c, int d) {
        long va = v[a], vb = v[b], vc = v[c], vd = v[d];
        va = va + vb + 2L * (va & 0xFFFFFFFFL) * (vb & 0xFFFFFFFFL);
        vd = Long.rotateRight(vd ^ va, 32);
        vc = vc + vd + 2L * (vc & 0xFFFFFFFFL) * (vd & 0xFFFFFFFFL);
        vb = Long.rotateRight(vb ^ vc, 24);
        va = va + vb + 2L * (va & 0xFFFFFFFFL) * (vb & 0xFFFFFFFFL);
        vd = Long.rotateRight(vd ^ va, 16);
        vc = vc + vd + 2L * (vc & 0xFFFFFFFFL) * (vd & 0xFFFFFFFFL);
        vb = Long.rotateRight(vb ^ vc, 63);
        v[a] = va; v[b] = vb; v[c] = vc; v[d] = vd;
    }

    // H' variable-length hash (RFC 9106 §3.2)
    private static byte[] hPrime(byte[] input, int outLen) {
        if (outLen <= 64) {
            Blake2b b = new Blake2b(outLen);
            b.update(le32(outLen));
            b.update(input);
            return b.digest();
        }
        byte[] result = new byte[outLen];
        int pos = 0;
        Blake2b b = new Blake2b(64);
        b.update(le32(outLen));
        b.update(input);
        byte[] a = b.digest();
        System.arraycopy(a, 0, result, 0, 32);
        pos = 32;
        while (outLen - pos > 64) {
            b = new Blake2b(64);
            b.update(a);
            a = b.digest();
            System.arraycopy(a, 0, result, pos, 32);
            pos += 32;
        }
        int remaining = outLen - pos;
        b = new Blake2b(remaining);
        b.update(a);
        a = b.digest();
        System.arraycopy(a, 0, result, pos, remaining);
        return result;
    }

    private static byte[] blockToBytes(long[] block) {
        byte[] out = new byte[BLOCK_SIZE * 8];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            long v = block[i];
            out[i*8]   = (byte) v;
            out[i*8+1] = (byte)(v >>> 8);
            out[i*8+2] = (byte)(v >>> 16);
            out[i*8+3] = (byte)(v >>> 24);
            out[i*8+4] = (byte)(v >>> 32);
            out[i*8+5] = (byte)(v >>> 40);
            out[i*8+6] = (byte)(v >>> 48);
            out[i*8+7] = (byte)(v >>> 56);
        }
        return out;
    }

    private static void bytesToBlock(long[] block, byte[] bytes) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            block[i] = ((long)(bytes[i*8]   & 0xFF))
                    | (((long)(bytes[i*8+1] & 0xFF)) << 8)
                    | (((long)(bytes[i*8+2] & 0xFF)) << 16)
                    | (((long)(bytes[i*8+3] & 0xFF)) << 24)
                    | (((long)(bytes[i*8+4] & 0xFF)) << 32)
                    | (((long)(bytes[i*8+5] & 0xFF)) << 40)
                    | (((long)(bytes[i*8+6] & 0xFF)) << 48)
                    | (((long)(bytes[i*8+7] & 0xFF)) << 56);
        }
    }

    private static byte[] le32(int v) {
        return new byte[]{(byte) v, (byte)(v >>> 8), (byte)(v >>> 16), (byte)(v >>> 24)};
    }

    // Standard Blake2b (RFC 7693) — used for H0 and H'
    private static final class Blake2b {
        private static final long[] IV = {
            0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L, 0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL, 0x5be0cd19137e2179L
        };
        private static final byte[][] SIGMA = {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15},
            {14,10, 4, 8, 9,15,13, 6, 1,12, 0, 2,11, 7, 5, 3},
            {11, 8,12, 0, 5, 2,15,13,10,14, 3, 6, 7, 1, 9, 4},
            { 7, 9, 3, 1,13,12,11,14, 2, 6, 5,10, 4, 0,15, 8},
            { 9, 0, 5, 7, 2, 4,10,15,14, 1,11,12, 6, 8, 3,13},
            { 2,12, 6,10, 0,11, 8, 3, 4,13, 7, 5,15,14, 1, 9},
            {12, 5, 1,15,14,13, 4,10, 0, 7, 6, 3, 9, 2, 8,11},
            {13,11, 7,14,12, 1, 3, 9, 5, 0,15, 4, 8, 6, 2,10},
            { 6,15,14, 9,11, 3, 0, 8,12, 2,13, 7, 1, 4,10, 5},
            {10, 2, 8, 4, 7, 6, 1, 5,15,11, 9,14, 3,12,13, 0},
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15},
            {14,10, 4, 8, 9,15,13, 6, 1,12, 0, 2,11, 7, 5, 3},
        };

        private final long[] h = new long[8];
        private final long[] counter = new long[2];
        private final byte[] buf = new byte[128];
        private int bufLen;
        private final int outLen;

        Blake2b(int outLen) {
            this.outLen = outLen;
            System.arraycopy(IV, 0, h, 0, 8);
            h[0] ^= 0x01010000L | outLen;
        }

        void update(byte[] in) {
            int off = 0;
            while (off < in.length) {
                if (bufLen == 128) {
                    addCounter(128);
                    compress(false);
                    bufLen = 0;
                }
                int take = Math.min(in.length - off, 128 - bufLen);
                System.arraycopy(in, off, buf, bufLen, take);
                bufLen += take;
                off += take;
            }
        }

        byte[] digest() {
            addCounter(bufLen);
            Arrays.fill(buf, bufLen, 128, (byte) 0);
            compress(true);
            byte[] out = new byte[outLen];
            for (int i = 0; i < outLen; i++) {
                out[i] = (byte)(h[i / 8] >>> ((i % 8) * 8));
            }
            return out;
        }

        private void addCounter(int n) {
            counter[0] += n;
            if (Long.compareUnsigned(counter[0], n) < 0) counter[1]++;
        }

        private void compress(boolean last) {
            long[] m = new long[16];
            for (int i = 0; i < 16; i++) m[i] = readLE64(buf, i * 8);
            long[] v = new long[16];
            System.arraycopy(h, 0, v, 0, 8);
            System.arraycopy(IV, 0, v, 8, 8);
            v[12] ^= counter[0];
            v[13] ^= counter[1];
            if (last) v[14] = ~v[14];
            for (byte[] s : SIGMA) {
                blake2G(v, 0, 4,  8, 12, m[s[0]],  m[s[1]]);
                blake2G(v, 1, 5,  9, 13, m[s[2]],  m[s[3]]);
                blake2G(v, 2, 6, 10, 14, m[s[4]],  m[s[5]]);
                blake2G(v, 3, 7, 11, 15, m[s[6]],  m[s[7]]);
                blake2G(v, 0, 5, 10, 15, m[s[8]],  m[s[9]]);
                blake2G(v, 1, 6, 11, 12, m[s[10]], m[s[11]]);
                blake2G(v, 2, 7,  8, 13, m[s[12]], m[s[13]]);
                blake2G(v, 3, 4,  9, 14, m[s[14]], m[s[15]]);
            }
            for (int i = 0; i < 8; i++) h[i] ^= v[i] ^ v[i + 8];
        }

        private static void blake2G(long[] v, int a, int b, int c, int d, long x, long y) {
            v[a] += v[b] + x;
            v[d] = Long.rotateRight(v[d] ^ v[a], 32);
            v[c] += v[d];
            v[b] = Long.rotateRight(v[b] ^ v[c], 24);
            v[a] += v[b] + y;
            v[d] = Long.rotateRight(v[d] ^ v[a], 16);
            v[c] += v[d];
            v[b] = Long.rotateRight(v[b] ^ v[c], 63);
        }

        private static long readLE64(byte[] b, int off) {
            return ((long)(b[off]   & 0xFF))
                 | (((long)(b[off+1] & 0xFF)) << 8)
                 | (((long)(b[off+2] & 0xFF)) << 16)
                 | (((long)(b[off+3] & 0xFF)) << 24)
                 | (((long)(b[off+4] & 0xFF)) << 32)
                 | (((long)(b[off+5] & 0xFF)) << 40)
                 | (((long)(b[off+6] & 0xFF)) << 48)
                 | (((long)(b[off+7] & 0xFF)) << 56);
        }
    }
}
