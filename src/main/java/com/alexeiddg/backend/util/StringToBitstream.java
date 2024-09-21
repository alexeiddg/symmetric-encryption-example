package com.alexeiddg.backend.util;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

@Service
public class StringToBitstream {

    // Apply ISO/IEC 7816-4 padding
    public static byte[][][] generateMatrixPadding(byte[] byteArray) {
        int chunkSize = 16;
        int paddingLength = chunkSize - (byteArray.length % chunkSize);
        int totalLength = byteArray.length + paddingLength;
        byte[] paddedByteArray = Arrays.copyOf(byteArray, totalLength);

        paddedByteArray[byteArray.length] = (byte) 0x80;

        int totalChunks = totalLength / chunkSize;
        byte[][][] matrixChunks = new byte[totalChunks][4][4];

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            byte[] chunk = Arrays.copyOfRange(paddedByteArray, start, start + chunkSize);

            byte[][] matrix = new byte[4][4];
            for (int j = 0; j < chunkSize; j++) {
                matrix[j / 4][j % 4] = chunk[j];
            }

            matrixChunks[i] = matrix;
        }

        return matrixChunks;
    }

    public static byte[][][] byteArrayToMatrixChunks(byte[] byteArray) {
        int chunkSize = 16;
        int totalLength = byteArray.length;
        int totalChunks = (int) Math.ceil((double) totalLength / chunkSize);
        byte[][][] matrixChunks = new byte[totalChunks][4][4];

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, totalLength);
            byte[] chunk = Arrays.copyOfRange(byteArray, start, end);

            byte[][] matrix = new byte[4][4];
            for (int j = 0; j < chunk.length; j++) {
                matrix[j / 4][j % 4] = chunk[j];
            }

            for (int j = chunk.length; j < chunkSize; j++) {
                matrix[j / 4][j % 4] = 0;
            }

            matrixChunks[i] = matrix;
        }

        return matrixChunks;
    }

    public static byte[] matrixChunksToByteArray(byte[][][] matrixChunks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[][] matrix : matrixChunks) {
            for (byte[] row : matrix) {
                outputStream.write(row, 0, row.length);
            }
        }

        byte[] fullByteArray = outputStream.toByteArray();

        // Remove ISO/IEC 7816-4 padding
        int i = fullByteArray.length - 1;
        while (i >= 0 && fullByteArray[i] == 0x00) {
            i--;
        }
        if (i >= 0 && fullByteArray[i] == (byte) 0x80) {
            return Arrays.copyOfRange(fullByteArray, 0, i);
        } else {
            throw new IllegalArgumentException("Invalid padding");
        }
    }

}
