package com.alexeiddg.backend.encryption.service;

import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;

@Service
public class CBCService {
        //FIXME:for some reason the code from cbc and reverse cbc is the same but using only the apply cbc method breaks the code :)

    public byte[][][] applyCBC(byte[][][] plaintextBlocks, KeyMatrix finalHashedKey) {
        byte[][][] resultBlocks = new byte[plaintextBlocks.length][4][4];
        byte[] finalKeyAsIV = finalHashedKey.flattenMatrix();
        byte[][] ivMatrix = KeyMatrix.rebuildMatrix(finalKeyAsIV).matrix();

        resultBlocks[0] = xorBlock(plaintextBlocks[0], ivMatrix);

        for (int i = 1; i < plaintextBlocks.length; i++) {
            resultBlocks[i] = xorBlock(plaintextBlocks[i], resultBlocks[i - 1]);
        }

        return resultBlocks;
    }

    public byte[][][] reverseCBC(byte[][][] ciphertextBlocks, KeyMatrix finalHashedKey) {
        byte[][][] plaintextBlocks = new byte[ciphertextBlocks.length][4][4];
        byte[] finalKeyAsIV = finalHashedKey.flattenMatrix();
        byte[][] ivMatrix = KeyMatrix.rebuildMatrix(finalKeyAsIV).matrix();

        plaintextBlocks[0] = xorBlock(ciphertextBlocks[0], ivMatrix);

        for (int i = 1; i < ciphertextBlocks.length; i++) {
            plaintextBlocks[i] = xorBlock(ciphertextBlocks[i], ciphertextBlocks[i - 1]);
        }

        return plaintextBlocks;
    }

    private byte[][] xorBlock(byte[][] block1, byte[][] block2) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) (block1[row][col] ^ block2[row][col]);
            }
        }
        return result;
    }

}
