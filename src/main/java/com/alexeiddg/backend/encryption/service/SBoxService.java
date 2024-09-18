package com.alexeiddg.backend.encryption.service;


import org.springframework.stereotype.Service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SBoxService {

    private static final int[] seedSBox = new int[256];

    static {
        // Initialize the seed S-box
        for (int i = 0; i < 256; i++) {
            seedSBox[i] = i;
        }
    }

    // Step 1: Generate dynamic S-box based on the key matrix
    public int[] generateDynamicSBox(KeyMatrix keyMatrix) {
        int[] dynamicSBox = Arrays.copyOf(seedSBox, seedSBox.length);

        // Flatten the 4x4 key matrix into a 16-byte array (128-bit key)
        byte[] flatKeyMatrix = flattenKeyMatrix(keyMatrix.matrix());

        // Generate row and column permutations based on the 4x4 key matrix
        List<Integer> keyDependentRows = generatePermutationFromKey(flatKeyMatrix);  // 4 rows for 4x4 matrix
        List<Integer> keyDependentColumns = generatePermutationFromKey(flatKeyMatrix);  // 4 columns for 4x4 matrix

        // Apply the permutation to rows and columns across the S-box
        dynamicSBox = permuteRowsAndColumns(dynamicSBox, keyDependentRows, keyDependentColumns);

        return dynamicSBox;
    }

    // Step 2: Generate inverse S-box for decryption
    public int[] generateInverseSBox(int[] dynamicSBox) {
        int[] inverseSBox = new int[256];
        for (int i = 0; i < 256; i++) {
            inverseSBox[dynamicSBox[i]] = i;  // Reverse mapping for decryption
        }
        return inverseSBox;
    }

    // Helper: Flatten a 4x4 key matrix into a single byte array
    private byte[] flattenKeyMatrix(byte[][] keyMatrix) {
        byte[] flatKeyMatrix = new byte[16];
        int index = 0;
        for (byte[] matrix : keyMatrix) {
            for (byte b : matrix) {
                flatKeyMatrix[index++] = b;
            }
        }
        return flatKeyMatrix;
    }

    // 1.1 - Helper: Generate permutation of the S-box based on the flattened key matrix
// Generate row and column permutations for 4x4 matrix
    private List<Integer> generatePermutationFromKey(byte[] flatKey) {
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            permutation.add(i);
        }

        // Use the flattened key to shuffle the list
        int seed = Arrays.hashCode(flatKey);
        Collections.shuffle(permutation, new java.util.Random(seed));

        System.out.println("Generated Permutation: " + permutation);

        return permutation;
    }


    // 1.2 - Helper: Permute rows and columns of the S-box
    private int[] permuteRowsAndColumns(int[] sBox, List<Integer> rowPermutation, List<Integer> colPermutation) {
        int[] permutedSBox = new int[sBox.length];

        for (int i = 0; i < sBox.length; i++) {
            int row = i / 16;
            int col = i % 16;

            // Get new row and column indices from the permutations
            int newRow = rowPermutation.get(row % 4);  // Limit to 4 rows
            int newCol = colPermutation.get(col % 4);  // Limit to 4 columns
            int newIndex = newRow * 16 + newCol;

            permutedSBox[newIndex] = sBox[i];
        }

        return permutedSBox;
    }

    // Step 3: Apply the S-box to a matrix of bytes (4x4 matrix)
    public byte[][] applySBoxToMatrix(byte[][] matrix, int[] sBox) {
        byte[][] transformedMatrix = new byte[matrix.length][matrix[0].length]; // Create a new matrix for the result

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                int byteValue = Byte.toUnsignedInt(matrix[row][col]);
                transformedMatrix[row][col] = (byte) sBox[byteValue]; // Apply S-box transformation
            }
        }

        return transformedMatrix; // Return transformed matrix for debugging
    }



    // Debug: Print the S-box in matrix format (16x16 grid)
    public void printSBox(byte[][] sBoxedMatrix) {
        for (byte[] row : sBoxedMatrix) {
            for (byte element : row) {
                System.out.printf("%02x ", element);
            }
            System.out.println();
        }
    }
}
