package com.alexeiddg.backend.encryption.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class CipherTextGenerator {

    private byte[] flattenMatrixChunks(byte[][][] matrixChunks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[][] matrix : matrixChunks) {
            for (byte[] row : matrix) {
                outputStream.write(row, 0, row.length);
            }
        }

        return outputStream.toByteArray();
    }

    public String returnCipherText(byte[][][] matrixChunks) {
        byte[] cipherBytes = flattenMatrixChunks(matrixChunks);
        return encodeToBase64(cipherBytes);
    }

    private String encodeToBase64(byte[] bytes) {
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}
