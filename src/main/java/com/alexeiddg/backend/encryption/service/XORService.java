package com.alexeiddg.backend.encryption.service;

import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;

@Service
public class XORService {

    public byte[][][] xorBitstream(byte[][][] bitstreamChunks, KeyMatrix keyMatrix) {
        byte[][][] resultChunks = new byte[bitstreamChunks.length][4][4];
        byte[][] keyMatrixData = keyMatrix.matrix();

        for (int chunk = 0; chunk < bitstreamChunks.length; chunk++) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    resultChunks[chunk][row][col] = (byte) (bitstreamChunks[chunk][row][col] ^ keyMatrixData[row][col]);
                }
            }
        }

        return resultChunks;
    }

}
