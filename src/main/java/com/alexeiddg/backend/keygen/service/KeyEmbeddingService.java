package com.alexeiddg.backend.keygen.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

import com.alexeiddg.backend.keygen.model.KeyMatrix;

@Service
public class KeyEmbeddingService {

    private static final SecureRandom random = new SecureRandom();

    public KeyMatrix embedKey(KeyMatrix entropyKey) {
        byte[][] matrix = entropyKey.matrix();

        int randomInt = random.nextInt(24) + 1;
        byte randomByte = (byte) randomInt;
        matrix[0][0] = randomByte;

        return new KeyMatrix(matrix);
    }

}
