package com.alexeiddg.backend.encryption.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexeiddg.backend.encryption.model.EncryptionResponse;
import com.alexeiddg.backend.util.StringToBitstream;
import com.alexeiddg.backend.keygen.model.KeyMatrix;
import com.alexeiddg.backend.keygen.service.KeyGenService;

import java.security.NoSuchAlgorithmException;

@Service
public class EncryptionService {

    @Autowired
    private KeyGenService keyGenService;

    @Autowired
    private XORService xorService;

    @Autowired
    private TransformService transformService;

    @Autowired
    private TransposeService transposeService;

    @Autowired
    private CipherTextGenerator cipherTextGenerator;


    private KeyMatrix getEncryptionKey() {
        return keyGenService.generateKey();
    }

    public EncryptionResponse encrypt(String text) throws NoSuchAlgorithmException {
        KeyMatrix encryptionKey = getEncryptionKey();

        // Extract the first byte from the matrix and use it as number
        assert encryptionKey != null;
        byte[][] keyMatrix = encryptionKey.matrix();
        byte firstByte = keyMatrix[0][0];
        int firstByteAsNumber = Byte.toUnsignedInt(firstByte);
        // TODO: delete this line
        System.out.println("First byte as number: " + firstByteAsNumber);

        // Encode the text to bitstream
        String bitstream = StringToBitstream.stringToBitstream(text);
        byte[][][] xorResult = StringToBitstream.bitstreamToMatrixChunks(bitstream);

        // TODO: return the intermediate steps
        // Perform XOR, Bit Shift, and Transpose
        for (int i = 0; i < firstByteAsNumber; i++) {
            xorResult = xorService.xorBitstream(xorResult, encryptionKey, i);

            // TODO: dynamic key based on rounds
            for (int j = 0; j < xorResult.length; j++) {
                xorResult[j] = transformService.performBitShift(xorResult[j], encryptionKey, true);
                xorResult[j] = transposeService.transposeMatrix(xorResult[j], encryptionKey);
            }
        }

        String clientKey = encryptionKey.flattenToHex();
        String ciphertText = cipherTextGenerator.returnCipherText(xorResult);
        return new EncryptionResponse(ciphertText, clientKey, firstByteAsNumber);
    }
}
