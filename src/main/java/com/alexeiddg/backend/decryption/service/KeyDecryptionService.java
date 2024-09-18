package com.alexeiddg.backend.decryption.service;

import org.springframework.stereotype.Service;

@Service
public class KeyDecryptionService {

    // Convert HEX to byte array
    public byte[] hexToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    // Create matrix from byte array
    public byte[][] createMatrixFromByteArray(byte[] byteArray) {
        if (byteArray.length != 32) {
            throw new IllegalArgumentException("Byte array must be 32 bytes long");
        }

        byte[][] matrix = new byte[4][8];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(byteArray, i * 8, matrix[i], 0, 8);
        }
        return matrix;
    }

    public int extractFirstByteAsNumber(byte[][] matrix) {
        byte firstByte = matrix[0][0];
        return Byte.toUnsignedInt(firstByte);
    }
}
