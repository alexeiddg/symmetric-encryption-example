package com.alexeiddg.backend.encryption.service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
    // Perform non-linear bit shift on the XORed matrix based on the key matrix
    public byte[][] performBitShift(byte[][] xorMatrix, KeyMatrix keyMatrix, boolean encrypt) {
        byte[][] keyMatrixData = keyMatrix.matrix();
        byte[][] shiftedMatrix = new byte[4][4];

        // Calculate shift amounts based on the key matrix
        int[] shiftAmounts = calculateShiftAmounts(keyMatrixData);

        // Apply non-linear bit shift to each row
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                shiftedMatrix[row][col] = (encrypt)
                        ? shiftLeft(xorMatrix[row][col], shiftAmounts[row])  // Encrypt: shift left
                        : shiftRight(xorMatrix[row][col], shiftAmounts[row]); // Decrypt: shift right
            }
        }

        return shiftedMatrix;
    }

    // Helper function to calculate shift amounts based on the key matrix
    private int[] calculateShiftAmounts(byte[][] keyMatrix) {
        int[] shiftAmounts = new int[4]; // One shift amount for each row

        for (int row = 0; row < 4; row++) {
            int rowSum = 0;
            for (int col = 0; col < 4; col++) {
                rowSum += Byte.toUnsignedInt(keyMatrix[row][col]); // Sum the values in each row
            }
            shiftAmounts[row] = rowSum % 8; // Limit shift amounts to between 0 and 7 bits (non-linear)
        }

        return shiftAmounts;
    }

    // Helper function to shift bits to the left
    private byte shiftLeft(byte value, int shiftAmount) {
        return (byte) ((value << shiftAmount) | ((value & 0xFF) >>> (8 - shiftAmount))); // Circular left shift
    }

    // Helper function to shift bits to the right
    private byte shiftRight(byte value, int shiftAmount) {
        return (byte) (((value & 0xFF) >>> shiftAmount) | (value << (8 - shiftAmount))); // Circular right shift
    }

}
