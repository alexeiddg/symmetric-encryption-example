package com.alexeiddg.backend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;

@Service
public class HashService {

    public KeyMatrix applySha256ToMatrix(KeyMatrix keyMatrix) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] flattenedKey = keyMatrix.flattenMatrix();
        byte[] hash = digest.digest(flattenedKey);
        byte[] truncatedHash = new byte[16];
        System.arraycopy(hash, 0, truncatedHash, 0, 16);

        return KeyMatrix.rebuildMatrix(truncatedHash);
    }
}
