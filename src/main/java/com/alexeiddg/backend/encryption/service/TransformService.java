package com.alexeiddg.backend.encryption.service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;
import org.springframework.stereotype.Service;

@Service
public class TransformService {

    public byte[][] performBitShift(byte[][] xorMatrix, KeyMatrix keyMatrix, boolean encrypt) {
        byte[][] keyMatrixData = keyMatrix.matrix();
        byte[][] shiftedMatrix = new byte[4][4];

        int[] shiftAmounts = calculateShiftAmounts(keyMatrixData);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                shiftedMatrix[row][col] = (encrypt)
                        ? shiftLeft(xorMatrix[row][col], shiftAmounts[row])
                        : shiftRight(xorMatrix[row][col], shiftAmounts[row]);
            }
        }

        return shiftedMatrix;
    }

    private int[] calculateShiftAmounts(byte[][] keyMatrix) {
        int[] shiftAmounts = new int[4];

        for (int row = 0; row < 4; row++) {
            int rowSum = 0;
            for (int col = 0; col < 4; col++) {
                rowSum += Byte.toUnsignedInt(keyMatrix[row][col]);
            }
            shiftAmounts[row] = rowSum % 8;
        }

        return shiftAmounts;
    }


    private byte shiftLeft(byte value, int shiftAmount) {
        return (byte) ((value << shiftAmount) | ((value & 0xFF) >>> (8 - shiftAmount)));
    }

    private byte shiftRight(byte value, int shiftAmount) {
        return (byte) (((value & 0xFF) >>> shiftAmount) | (value << (8 - shiftAmount)));
    }

}
