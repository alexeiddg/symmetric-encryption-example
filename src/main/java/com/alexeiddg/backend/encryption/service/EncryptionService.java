package com.alexeiddg.backend.encryption.service;

import com.alexeiddg.backend.encryption.util.CipherTextGenerator;
import com.alexeiddg.backend.util.StringToBitstream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexeiddg.backend.encryption.model.EncryptionResponse;
import com.alexeiddg.backend.util.HashService;
import com.alexeiddg.backend.keygen.model.KeyMatrix;
import com.alexeiddg.backend.keygen.service.KeyGenService;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;


@Service
public class EncryptionService {

    @Autowired
    private KeyGenService keyGenService;

    @Autowired
    private SBoxService sBoxService;

    @Autowired
    private XORService xorService;

    @Autowired
    private TransformService transformService;

    @Autowired
    private TransposeService transposeService;

    @Autowired
    private CBCService cbcService;

    @Autowired
    private CipherTextGenerator cipherTextGenerator;

    @Autowired
    private HashService hashService;

    private KeyMatrix getEncryptionKey() {
        return keyGenService.generateKey();
    }

    public EncryptionResponse encrypt(String text) throws NoSuchAlgorithmException {

        KeyMatrix encryptionKey = getEncryptionKey();

        assert encryptionKey != null;
        byte[][] keyMatrix = encryptionKey.matrix();
        byte firstByte = keyMatrix[0][0];
        int firstByteAsNumber = Byte.toUnsignedInt(firstByte);
        String clientKey = encryptionKey.flattenToHex();

        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[][][] xorResult = StringToBitstream.generateMatrixPadding(textBytes);

        byte[] keyBytes = encryptionKey.flattenMatrix();
        byte[] sBox = SBoxService.generateSBox(keyBytes);
        xorResult = sBoxService.applySBoxSubstitution(xorResult, sBox);

        for (int i = 0; i < firstByteAsNumber; i++) {
            xorResult = xorService.xorBitstream(xorResult, encryptionKey);

            for (int j = 0; j < xorResult.length; j++) {
                xorResult[j] = transformService.performBitShift(xorResult[j], encryptionKey, true);
                xorResult[j] = transposeService.transposeMatrix(xorResult[j], encryptionKey);
            }

            encryptionKey = hashService.applySha256ToMatrix(encryptionKey);
        }

        xorResult = cbcService.applyCBC(xorResult, encryptionKey);
        String cipherText = cipherTextGenerator.returnCipherText(xorResult);

        return new EncryptionResponse(cipherText, clientKey, firstByteAsNumber);
    }
}
