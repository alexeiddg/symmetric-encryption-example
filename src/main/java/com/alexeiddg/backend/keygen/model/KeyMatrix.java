package com.alexeiddg.backend.keygen.model;

public record KeyMatrix(byte[][] matrix) {
    public KeyMatrix {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Matrix must be 4x4 in size for a 128-bit key");
        }
    }

    public String flattenToHex() {
        StringBuilder hexString = new StringBuilder();
        for (byte[] row : matrix) {
            for (byte element : row) {
                hexString.append(String.format("%02x", element));
            }
        }
        return hexString.toString();
    }

    public byte[] flattenMatrix() {
        byte[] flatKey = new byte[16];
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                flatKey[index++] = matrix[row][col];
            }
        }
        return flatKey;
    }

    public static KeyMatrix rebuildMatrix(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Array must be 16 bytes to form a 4x4 matrix");
        }
        byte[][] matrix = new byte[4][4];
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                matrix[row][col] = array[index++];
            }
        }
        return new KeyMatrix(matrix);
    }
}
