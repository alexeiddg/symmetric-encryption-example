package com.alexeiddg.backend.encryption.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;
import com.alexeiddg.backend.util.HashService;

import java.security.NoSuchAlgorithmException;

@Service
public class XORService {

    @Autowired
    private HashService hashService;

    public byte[][][] xorBitstream(byte[][][] bitstreamChunks, KeyMatrix keyMatrix, int iter) throws NoSuchAlgorithmException {
        byte[][][] resultChunks = new byte[bitstreamChunks.length][4][4];

        for (int i = 0; i < iter; i++) {
            byte[] flattenedKey = keyMatrix.flattenMatrix();

            flattenedKey = hashService.applySha256ToMatrix(KeyMatrix.rebuildMatrix(flattenedKey)).flattenMatrix();
            keyMatrix = KeyMatrix.rebuildMatrix(flattenedKey);

            byte[][] keyMatrixData = keyMatrix.matrix();
            for (int chunk = 0; chunk < bitstreamChunks.length; chunk++) {
                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 4; col++) {
                        resultChunks[chunk][row][col] = (byte) (bitstreamChunks[chunk][row][col] ^ keyMatrixData[row][col]);
                    }
                }
            }
        }

        return resultChunks;
    }
}
