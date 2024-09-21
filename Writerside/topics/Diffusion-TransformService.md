# Diffusion: TransformService

Overview
--------

The `TransformService` is responsible for performing bitwise shifts on the encrypted data matrix (produced after the XOR operation) during both encryption and decryption. It uses a calculated shift amount derived from the key matrix to modify the data matrix by shifting the bits of each byte either left (during encryption) or right (during decryption).

This service enhances the complexity of the encryption process by altering the bit-level structure of the data matrix, ensuring that even small changes to the key or data result in drastically different encrypted outputs.

```
public class TransformService {

    public byte[][] performBitShift(byte[][] xorMatrix, KeyMatrix keyMatrix, boolean encrypt) {
        byte[][] keyMatrixData = keyMatrix.matrix();
        byte[][] shiftedMatrix = new byte[4][4];

        int[] shiftAmounts = calculateShiftAmounts(keyMatrixData);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                shiftedMatrix[row][col] = (encrypt)
                        ? shiftLeft(xorMatrix[row][col], shiftAmounts[row])
                        : shiftRight(xorMatrix[row][col], shiftAmounts[row]);
            }
        }

        return shiftedMatrix;
    }

    private int[] calculateShiftAmounts(byte[][] keyMatrix) {
        int[] shiftAmounts = new int[4];

        for (int row = 0; row < 4; row++) {
            int rowSum = 0;
            for (int col = 0; col < 4; col++) {
                rowSum += Byte.toUnsignedInt(keyMatrix[row][col]);
            }
            shiftAmounts[row] = rowSum % 8;
        }

        return shiftAmounts;
    }

    private byte shiftLeft(byte value, int shiftAmount) {
        return (byte) ((value << shiftAmount) | ((value & 0xFF) >>> (8 - shiftAmount)));
    }

    private byte shiftRight(byte value, int shiftAmount) {
        return (byte) (((value & 0xFF) >>> shiftAmount) | (value << (8 - shiftAmount)));
    }

}
```

### Purpose and Role in the Encryption Service

-   **Bit-Level Manipulation**: The `TransformService` manipulates the individual bits of each byte in the data matrix, making the encryption process more resistant to cryptanalysis.
-   **Differentiation Between Encryption and Decryption**: The service shifts bits to the left during encryption and to the right during decryption, ensuring that the transformation can be reversed to recover the original data.
-   **Key-Dependent Shifts**: The amount of shift for each row of the matrix is determined by the sum of the values in the corresponding row of the key matrix. This means that changes in the key matrix directly influence how the data is shifted.

### Key Concepts

-   **Bit Shifting**: A bitwise operation that moves the bits of a byte either to the left or right by a certain number of positions. Shifting bits left introduces zeros on the right, while shifting bits right introduces zeros on the left.
-   **Key-Dependent Shifting**: The number of bit shifts is determined by the sum of the values in each row of the key matrix. This sum is then reduced modulo 8 to ensure the shift amount stays within the range of a byte (0 to 7 shifts).
    -   **Encrypt Shift**: Bits are shifted left.
    -   **Decrypt Shift**: Bits are shifted right.

### Description

-   **Objective**: Perform key-dependent bit shifts on the data matrix to further scramble the encrypted data during encryption, and reverse the shifts during decryption.

-   **Process**:

    1.  **Shift Amount Calculation**:
        -   For each row of the key matrix, the sum of all the byte values in that row is computed.
        -   This sum is then taken modulo 8 to produce a shift amount for that row (since shifting a byte more than 8 times wraps back around).
    2.  **Bit Shifting**:
        -   Depending on whether the `encrypt` flag is set to `true` or `false`, the bits in each byte of the data matrix (xorMatrix) are shifted either to the left (during encryption) or to the right (during decryption) by the calculated shift amount for that row.
        -   The bit shift operations ensure that the data is further transformed after the XOR operation, making it more difficult to reverse without the correct key and transformation process.
    3.  **Matrix Transformation**:
        -   The matrix is processed row by row, with each byte in the row undergoing a shift operation according to the row's shift amount. The result is a new matrix, referred to as the shifted matrix, which contains the bitwise-transformed data.

### Example

#### Encryption (Shift Left)

Let's assume the following matrix after XOR:

`XOR Matrix:
[42, 15, 22, 37]
[60, 128, 90, 44]
[17, 99, 77, 64]
[255, 45, 33, 21]`

And the key matrix:

`Key Matrix:
[137, 20, 30, 50]
[10,  11, 9,  3]
[60,  10, 33, 22]
[19,  5,  89, 70]`

Step 1: **Calculate Shift Amounts**:

-   Row 1: Sum = `137 + 20 + 30 + 50 = 237` → Shift amount = `237 % 8 = 5`
-   Row 2: Sum = `10 + 11 + 9 + 3 = 33` → Shift amount = `33 % 8 = 1`
-   Row 3: Sum = `60 + 10 + 33 + 22 = 125` → Shift amount = `125 % 8 = 5`
-   Row 4: Sum = `19 + 5 + 89 + 70 = 183` → Shift amount = `183 % 8 = 7`

Step 2: **Apply Left Shifts**:

-   Row 1: Shift each byte left by 5 bits.
-   Row 2: Shift each byte left by 1 bit.
-   Row 3: Shift each byte left by 5 bits.
-   Row 4: Shift each byte left by 7 bits.

Resulting shifted matrix after encryption:

`Shifted Matrix (after left shifts):
[224, 240, 176, 192]
[120, 0, 180, 88]
[136, 198, 144, 128]
[254, 90, 66, 42]`

#### Decryption (Shift Right)

During decryption, the same shift amounts are used, but the bits are shifted to the right instead of left. This reverses the transformation and recovers the original matrix after XOR.

### Matrix Operations Explained

-   **Shift Amount Calculation**:
    -   The shift amount for each row in the matrix is determined by the sum of the values in the corresponding row of the key matrix. This sum is taken modulo 8 to limit the shift amount to a valid range (0-7 shifts).
-   **Bitwise Shifts**:
    -   Each byte in the data matrix is subjected to a left or right bit shift, depending on whether the operation is encryption or decryption.
    -   During encryption, bits are shifted left by the calculated amount, while during decryption, the bits are shifted right by the same amount.

### Role in the Encryption Workflow

-   **During Encryption**:
    -   After the XOR operation, the matrix is further scrambled by shifting the bits of each byte. This transformation, based on the key matrix, ensures that even similar input matrices produce drastically different encrypted outputs.
-   **During Decryption**:
    -   The transformation is reversed by shifting the bits of each byte back to their original positions, using the same shift amounts calculated from the key matrix. This ensures that the data can be correctly decrypted.

### Integration in the Encryption Service

-   **Bit Shift Layer**:

    -   The `performBitShift` function adds another layer of complexity to the encryption process, making the encryption harder to break by introducing bit-level changes based on the key matrix.
-   **Multi-round Encryption**:

    -   As with the XOR operation, the bit shifts are applied during each round of encryption, and the key evolves with each round. The cumulative effect of the bit shifts and key changes makes it extremely difficult to reverse the encryption process without the correct key and shift amounts.
-   **Reversibility**:

    -   The bit shifts are reversible. During decryption, the same process is applied in reverse (using right shifts), ensuring the original data can be accurately recovered.