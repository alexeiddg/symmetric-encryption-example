package com.alexeiddg.backend.encryption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexeiddg.backend.encryption.service.EncryptionService;
import com.alexeiddg.backend.encryption.model.EncryptionResponse;
import com.alexeiddg.backend.encryption.model.EncryptionRequest;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api")
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public EncryptionResponse thing(@RequestBody EncryptionRequest request) throws NoSuchAlgorithmException {
        EncryptionResponse response = encryptionService.encrypt(request.getText());
        if (response != null) {
            System.out.println("Spring Server - Response OK");
        }
        return response;
    }


    /*
    //TODO: REMOVE KEY DEBUG

        // Decrypt each chunk
        // Reverse the Bit Shift
        System.out.println("Transpose Reversal ----------------- ");
        for (int i = 0; i < xorResult.length; i++) {
            xorResult[i] = transposeService.reverseTransposeMatrix(xorResult[i], encryptionKey); // Reverse Transpose
        }
        cipherTextGenerator.printCipherText(xorResult);

        System.out.println("Bit Shift Reversal ----------------- ");
        for (int i = 0; i < xorResult.length; i++) {
            xorResult[i] = transformService.performBitShift(xorResult[i], encryptionKey, false);  // Decrypt = false (shift right)
        }
        cipherTextGenerator.printCipherText(xorResult);

        System.out.println("XOR Reversal ----------------- ");
        byte[][][] xorReversed = xorService.xorBitstream(xorResult, encryptionKey);
        cipherTextGenerator.printCipherText(xorReversed);

        // Convert chunks back to bitstream and then to text
        String decryptedBits = StringToBitstream.matrixChunksToBitstream(xorReversed);
        String decryptedText = StringToBitstream.bitstreamToString(decryptedBits);
        System.out.println("Decrypted text: " + decryptedText);
        System.out.println("Decrypted bitstream length: " + decryptedBits.length());
    */
}
