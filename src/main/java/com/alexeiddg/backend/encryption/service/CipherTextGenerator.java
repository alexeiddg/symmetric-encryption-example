package com.alexeiddg.backend.encryption.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alexeiddg.backend.util.StringToBitstream;

@Service
public class CipherTextGenerator {

    @Autowired
    private StringToBitstream stringToBitstream;

    // Generate the ciphertext for the given matrix (2D or 3D)
    public String generateCipherText(Object matrix) {
        StringBuilder cipherText = new StringBuilder();

        // If the matrix is 3D (chunks), process each chunk
        if (matrix instanceof byte[][][] matrixChunks) {
            for (byte[][] chunk : matrixChunks) {
                // Convert each chunk to bitstream and append to cipherText
                String chunkBitstream = StringToBitstream.matrixToBitstream(chunk);
                cipherText.append(chunkBitstream);
            }
        } else if (matrix instanceof byte[][] singleMatrix) {
            // If it's a single 2D matrix, convert it directly
            String matrixBitstream = StringToBitstream.matrixToBitstream(singleMatrix);
            cipherText.append(matrixBitstream);
        }

        return cipherText.toString(); // Return the final ciphertext as a bitstream string
    }

    public String returnCipherText(Object matrix) {
        String cipherText = generateCipherText(matrix);
        return encodeToBase64(cipherText.getBytes());
    }

    private String encodeToBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    private String encodeToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
