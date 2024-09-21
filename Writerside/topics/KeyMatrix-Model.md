# KeyMatrix Model

Overview
--------

The `KeyMatrix` class represents the encryption key as a 4x4 matrix of bytes (16 bytes in total). This matrix-based representation is crucial for matrix-based cryptographic operations such as XOR, transpositions, and S-Box substitutions used in the encryption and decryption services. The class provides methods to flatten the matrix into a byte array or hexadecimal string and to reconstruct the matrix from a byte array.

```
public record KeyMatrix(byte[][] matrix) {
    public KeyMatrix {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Matrix must be 4x4 in size for a 128-bit key");
        }
    }
```

Purpose and Role in the Encryption Service
------------------------------------------

-   **Matrix Representation of the Key**: The encryption key is stored in a 4x4 matrix, which allows for more complex matrix-based transformations during encryption and decryption. This structure enhances the security of the encryption algorithm.
-   **Conversion to and from Matrix**: The class includes methods to flatten the matrix into a linear byte array or a hexadecimal string, allowing it to be passed between different encryption methods that require linear data, and then to rebuild the matrix for further operations.

Key Concepts
------------

-   **Matrix Representation**: A 4x4 matrix of bytes is used to represent the encryption key. This matrix is essential for applying matrix-based cryptographic operations.
-   **128-bit Key**: The matrix contains 16 bytes (128 bits), which is the size of a standard encryption key in many cryptographic algorithms.
-   **Matrix Flattening**: The process of converting a 4x4 matrix into a 1D byte array for cryptographic operations that require linear input data.
-   **Matrix Reconstruction**: The process of rebuilding the matrix from a 16-byte array, enabling the matrix format to be reused after cryptographic operations.

### KeyMatrix Constructor

**Objective**: Ensure that the matrix provided is always 4x4 in size, maintaining the 128-bit key format.

**Process**:

-   Checks that the provided matrix has 4 rows and 4 columns.
-   Throws an `IllegalArgumentException` if the matrix size is not exactly 4x4.

**Result**: Creates a `KeyMatrix` object if the matrix is valid.

* * * * *

### `flattenToHex()`

**Objective**: Convert the 4x4 byte matrix into a hexadecimal string.

```
    public String flattenToHex() {
        StringBuilder hexString = new StringBuilder();
        for (byte[] row : matrix) {
            for (byte element : row) {
                hexString.append(String.format("%02x", element));
            }
        }
        return hexString.toString();
    }
```

**Process**:

1.  **Matrix Iteration**:
    -   Iterates over each row and column of the matrix.
2.  **Hex Conversion**:
    -   Each byte is converted to its 2-character hexadecimal representation.
    -   The hexadecimal string is built row by row from the matrix.

**Result**: A hexadecimal string representation of the entire matrix.

* * * * *

### `flattenMatrix()`

**Objective**: Flatten the 4x4 matrix into a 1D byte array for cryptographic operations.

```
    public byte[] flattenMatrix() {
        byte[] flatKey = new byte[16];
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                flatKey[index++] = matrix[row][col];
            }
        }
        return flatKey;
    }
```

**Process**:

1.  **Matrix Traversal**:
    -   Iterates over each row and column of the matrix.
    -   The values from the matrix are copied into a 16-byte array, ensuring that each value maintains its position in the original matrix.
2.  **Linear Key Representation**:
    -   The result is a 1D byte array, allowing the matrix to be passed into cryptographic functions that require a linear data format, such as hashing or XOR.

**Result**: A byte array containing all 16 bytes of the matrix in row-major order.

* * * * *

### `rebuildMatrix(byte[] array)`

**Objective**: Convert a 1D byte array back into a 4x4 matrix.

```
    public static KeyMatrix rebuildMatrix(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Array must be 16 bytes to form a 4x4 matrix");
        }
        byte[][] matrix = new byte[4][4];
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                matrix[row][col] = array[index++];
            }
        }
        return new KeyMatrix(matrix);
    }
```

**Process**:

1.  **Validation**:
    -   The function checks if the byte array length is 16. If not, it throws an `IllegalArgumentException`.
2.  **Matrix Reconstruction**:
    -   The byte array is split into chunks, each corresponding to a row of the matrix.
    -   The first four bytes fill the first row, the next four bytes fill the second row, and so on.

**Result**: A new `KeyMatrix` object reconstructed from the 1D byte array.

* * * * *

Role in the Encryption Workflow
-------------------------------

-   **During Encryption**:
    -   The `KeyMatrix` is used as the encryption key, providing a structured format for applying cryptographic transformations.
    -   After each encryption round, the matrix is hashed and updated using matrix-based transformations like XOR and S-Box substitutions.
-   **During Decryption**:
    -   The `KeyMatrix` is reconstructed from the byte array form, allowing for matrix-based transformations to reverse the encryption process.

Matrix Operations Explained
---------------------------

-   **Matrix Flattening**:
    -   The 4x4 matrix is flattened into a 1D byte array to allow for cryptographic operations that require a linear data format, such as hashing or encryption.
-   **Matrix Rebuilding**:
    -   The reverse process, where a 1D byte array is used to reconstruct a 4x4 matrix. This ensures that matrix-based transformations can be reapplied during decryption.

Integration in Encryption Service
---------------------------------

-   **Key Representation**:
    -   The `KeyMatrix` provides a structured way to store and manipulate the encryption key as a matrix. This matrix-based format is used for applying transformations such as XOR, transposition, and S-Box substitutions.
-   **Key Transformation**:
    -   After each encryption round, the key is updated and transformed using matrix-based cryptographic operations. The key evolves with each round, ensuring that each round uses a different key.
-   **Reusability Across Rounds**:
    -   The matrix can be flattened, hashed, and reconstructed as needed, allowing the key to be updated and reused in the next encryption or decryption round.