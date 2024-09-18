package com.alexeiddg.backend.decryption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexeiddg.backend.decryption.service.KeyDecryptionService;
import com.alexeiddg.backend.keygen.model.KeyMatrix;

import java.util.Arrays;

@RestController
@RequestMapping("/decrypt")
public class DecryptionController {


    @Autowired
    private KeyDecryptionService keyDecryptionService;

    // Create matrix from HEX key
    private KeyMatrix getKeyMatrix(String hexKey) {
        byte[] byteArray = keyDecryptionService.hexToByteArray(hexKey);
        byte[][] matrix = keyDecryptionService.createMatrixFromByteArray(byteArray);
        return new KeyMatrix(matrix);
    }

    // Extract repetition count
    private int getRepetitionCount(String hexKey) {
        KeyMatrix keyMatrix = getKeyMatrix(hexKey);
        return keyDecryptionService.extractFirstByteAsNumber(keyMatrix.matrix());
    }

    // Test Decryption
    @GetMapping("/key")
    private void decryptKey() {
        // Pass the actual HEX key for decryption
        String hexKey = "15a23fdcc367f8c3b84e0447fb46767d3f07a80fca055350d118d7fa3e26fbed";

        // Extract repetition count
        int repetitionCount = getRepetitionCount(hexKey);
        System.out.println("Repetition count: " + repetitionCount);

        // Print the matrix
        KeyMatrix keyMatrix = getKeyMatrix(hexKey);
        System.out.println("Matrix: " + Arrays.deepToString(keyMatrix.matrix()));
    }

}
