package com.alexeiddg.backend.encryption.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class SBoxService {

    public static byte[] generateSBox(byte[] key) throws NoSuchAlgorithmException {
        byte[] sBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            sBox[i] = (byte) i;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] keyHash = md.digest(key);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(keyHash);

        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);
            byte temp = sBox[i];
            sBox[i] = sBox[j];
            sBox[j] = temp;
        }

        return sBox;
    }

    public byte[][][] applySBoxSubstitution(byte[][][] matrixChunks, byte[] sBox) {
        for (byte[][] matrix : matrixChunks) {
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    int byteValue = Byte.toUnsignedInt(matrix[row][col]);
                    matrix[row][col] = sBox[byteValue];
                }
            }
        }
        return matrixChunks;
    }

    public static byte[] generateInverseSBox(byte[] sBox) {
        byte[] inverseSBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            inverseSBox[sBox[i] & 0xFF] = (byte) i;
        }
        return inverseSBox;
    }
}
