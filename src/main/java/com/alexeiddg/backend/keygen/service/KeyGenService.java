package com.alexeiddg.backend.keygen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;
import com.alexeiddg.backend.util.EntropyHandler;

@Service
public class KeyGenService {

    @Autowired
    private EntropyHandler entropyHandler;

    @Autowired
    private KeyEmbeddingService keyEmbeddingService;

    public KeyMatrix generateKeyFromEntropy() {
        assert entropyHandler != null;
        byte[] normalizedEntropy = entropyHandler.getEntropy();

        if (normalizedEntropy.length != 16) {
            throw new IllegalArgumentException("Entropy must be 16 bytes long");
        }

        byte[][] matrix = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(normalizedEntropy, i * 4, matrix[i], 0, 4);
        }

        return new KeyMatrix(matrix);
    }

    public KeyMatrix embedKey(KeyMatrix entropyKey) {
        return keyEmbeddingService.embedKey(entropyKey);
    }

    public KeyMatrix generateKey() {
        KeyMatrix key = generateKeyFromEntropy();
        return embedKey(key);
    }
}
