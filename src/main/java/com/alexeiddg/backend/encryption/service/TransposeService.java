package com.alexeiddg.backend.encryption.service;

import com.alexeiddg.backend.keygen.model.KeyMatrix;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;

@Service
public class TransposeService {

    private int[] flattenKeyMatrix(byte[][] keyMatrix) {
        int[] flatKeyMatrix = new int[16];
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                flatKeyMatrix[index++] = Byte.toUnsignedInt(keyMatrix[row][col]);
            }
        }
        return flatKeyMatrix;
    }

    // Step 2: Generate a permutation for rows/columns based on the flattened key matrix
    private int[] generatePermutationFromKey(int[] flatKeyMatrix) {
        Integer[] indices = {0, 1, 2, 3};  // For a 4x4 matrix
        Arrays.sort(indices, Comparator.comparingInt(a -> flatKeyMatrix[a]));
        return Arrays.stream(indices).mapToInt(Integer::intValue).toArray();
    }

    // Step 3: Apply transposition (rearrange rows and columns) based on the generated permutations
    public byte[][] transposeMatrix(byte[][] matrix, KeyMatrix keyMatrix) {
        byte[][] transposedMatrix = new byte[4][4];

        // Flatten the key matrix and generate permutations for rows and columns
        int[] flatKeyMatrix = flattenKeyMatrix(keyMatrix.matrix());
        int[] rowPerm = generatePermutationFromKey(flatKeyMatrix);
        int[] colPerm = generatePermutationFromKey(flatKeyMatrix);

        // Apply the row and column permutations to the matrix
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                transposedMatrix[rowPerm[row]][colPerm[col]] = matrix[row][col];
            }
        }
        return transposedMatrix;
    }

    // Step 4: Reverse the transposition to return the matrix to its original state
    public byte[][] reverseTransposeMatrix(byte[][] matrix, KeyMatrix keyMatrix) {
        byte[][] reversedMatrix = new byte[4][4];

        // Flatten the key matrix and generate permutations for rows and columns
        int[] flatKeyMatrix = flattenKeyMatrix(keyMatrix.matrix());
        int[] rowPerm = generatePermutationFromKey(flatKeyMatrix);
        int[] colPerm = generatePermutationFromKey(flatKeyMatrix);

        // Apply the reverse row and column permutations
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                reversedMatrix[row][col] = matrix[rowPerm[row]][colPerm[col]];
            }
        }
        return reversedMatrix;
    }
}
