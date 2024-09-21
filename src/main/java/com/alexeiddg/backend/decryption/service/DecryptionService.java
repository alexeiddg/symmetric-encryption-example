package com.alexeiddg.backend.decryption.service;

import com.alexeiddg.backend.encryption.service.*;
import com.alexeiddg.backend.keygen.model.KeyMatrix;
import com.alexeiddg.backend.util.StringToBitstream;
import com.alexeiddg.backend.util.HashService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.alexeiddg.backend.encryption.service.SBoxService.generateInverseSBox;
import static com.alexeiddg.backend.encryption.service.SBoxService.generateSBox;

@Service
public class ecryptionService {

    @Autowired
    private SBoxService sBoxService;
    @Autowired
    private TransposeService transposeService;

    @Autowired
    private TransformService transformService;

    @Autowired
    private XORService xorService;

    @Autowired
    private HashService hashService;

    @Autowired
    private CBCService cbcService;
    @Autowired
    private CipherTextGenerator cipherTextGenerator;

    // Main decryption method
    public String decrypt(String clientKey, String ciphertext) throws Exception {

        // FIXME: Matrices Now match
        byte[] decodedCiphertext = Base64.getDecoder().decode(ciphertext);
        byte[][][] cipherMatrixChunks = StringToBitstream.byteArrayToMatrixChunks(decodedCiphertext);

        // FIXME: decode the key
        byte[] byteArray = hexToByteArray(clientKey);
        KeyMatrix decodedKey = KeyMatrix.rebuildMatrix(byteArray);
        byte[] original_keyBytes = decodedKey.flattenMatrix();

        int rounds = extractFirstByteAsNumber(decodedKey);

        KeyMatrix finalHashedKey = decodedKey;
        for (int i = 0; i < rounds; i++) {
            finalHashedKey = hashService.applySha256ToMatrix(finalHashedKey);
        }
        StringToBitstream.printMatrix(finalHashedKey.matrix());

        cipherMatrixChunks = cbcService.reverseCBC(cipherMatrixChunks, finalHashedKey);

        for (int i = 0; i < rounds; i++) {
            for (int j = 0; j < cipherMatrixChunks.length; j++) {
                cipherMatrixChunks[j] = transposeService.reverseTransposeMatrix(cipherMatrixChunks[j], decodedKey);
                cipherMatrixChunks[j] = transformService.performBitShift(cipherMatrixChunks[j], decodedKey, false);
            }

            cipherMatrixChunks = xorService.xorBitstream(cipherMatrixChunks, decodedKey);
            decodedKey = hashService.applySha256ToMatrix(decodedKey);
        }

        byte[] sBox = generateSBox(original_keyBytes);
        byte[] inverseSBox = generateInverseSBox(sBox);
        cipherMatrixChunks = sBoxService.applySBoxSubstitution(cipherMatrixChunks, inverseSBox);

        byte[] plainBytes = StringToBitstream.matrixChunksToByteArray(cipherMatrixChunks);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }


    private byte[] hexToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private int extractFirstByteAsNumber(KeyMatrix key) {
        byte[][] keyMatrix = key.matrix();
        byte firstByte = keyMatrix[0][0];
        return Byte.toUnsignedInt(firstByte);
    }
}
