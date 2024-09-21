# Util: CipherTextGenerator

Overview
--------

The `CipherTextGenerator` class is responsible for the final step of the encryption process: converting a matrix representation of the ciphertext into a Base64-encoded string. This process is crucial for transmitting the encrypted data as a compact, printable format that can be easily transferred over communication channels (such as HTTP requests).

```
    private byte[] flattenMatrixChunks(byte[][][] matrixChunks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[][] matrix : matrixChunks) {
            for (byte[] row : matrix) {
                outputStream.write(row, 0, row.length);
            }
        }

        return outputStream.toByteArray();
    }

    public String returnCipherText(byte[][][] matrixChunks) {
        byte[] cipherBytes = flattenMatrixChunks(matrixChunks);
        return encodeToBase64(cipherBytes);
    }

    private String encodeToBase64(byte[] bytes) {
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
```

Key Concepts
------------

-   **Matrix Flattening**: The class converts the 3D matrix representation of ciphertext into a linear byte array.
-   **Base64 Encoding**: After flattening the matrix, the byte array is encoded into a Base64 string for easy transmission and storage.
-   **Ciphertext Generation**: The output is a Base64-encoded string representing the encrypted data, ready to be returned to the client or stored securely.

Methods
-------

### `flattenMatrixChunks`

#### Objective

To flatten the 3D matrix of ciphertext into a single byte array for further encoding.

#### Process

1.  **Matrix Traversal**:

    -   The 3D matrix (`matrixChunks`) contains multiple 4x4 byte matrices, representing blocks of ciphertext.
    -   The method loops through each matrix, row by row, and writes each row of bytes to a `ByteArrayOutputStream`.
2.  **Flattening**:

    -   As it iterates through the rows of each matrix, it concatenates them into a single byte array.
    -   This results in a 1D byte array that contains the serialized form of the 3D matrix.

#### Matrix Operations

-   The 3D matrix is structured as follows:

    -   **MatrixChunks**: An array of ciphertext blocks.
    -   **Each Block**: A 4x4 matrix (16 bytes) representing part of the ciphertext.
-   The flattening process:

    `[Matrix 1]          [Matrix 2]          [Matrix 3]          ...
    [Row 1] [Row 2] ... [Row 1] [Row 2] ... [Row 1] [Row 2] ...

    Resulting flattened byte array:
    ByteArray -> [M1 Row 1] [M1 Row 2] ... [M2 Row 1] [M2 Row 2] ... [Mn Row 4]`

-   This operation ensures that the 3D matrix structure is converted into a sequential byte array, ready for encoding.

### `returnCipherText`

#### Objective {id="objective_2"}

To take the flattened matrix of ciphertext and return it as a Base64-encoded string.

#### Process {id="process_2"}

1.  **Flatten the Matrix**:

    -   Calls `flattenMatrixChunks` to convert the 3D matrix into a linear byte array.
2.  **Encode the Bytes**:

    -   Calls `encodeToBase64` to transform the byte array into a Base64-encoded string.
3.  **Return**:

    -   The Base64 string is returned as the final ciphertext.

#### Role in the Encryption Workflow

-   After all transformations, XOR operations, and CBC chaining have been applied to the matrix, this method finalizes the encryption process by transforming the matrix back into a format that can be easily stored or transmitted.

### `encodeToBase64`

#### Objective {id="objective_1"}

To convert a byte array into a Base64 string for easy transmission and storage.

#### Process {id="process_1"}

1.  **Base64 Encoding**:

    -   Converts the input byte array into a Base64 URL-safe string using the `Base64.getUrlEncoder().encodeToString` method.
2.  **Why Base64?**:

    -   Base64 encoding ensures that the binary data is represented in a printable format. This is essential when transmitting encrypted data over channels that do not support binary (e.g., JSON, HTTP).

#### Result

-   The final Base64 string represents the encoded ciphertext, ready to be sent as output or stored.

Role in the Encryption Workflow {id="role-in-the-encryption-workflow_1"}
-------------------------------

-   **Final Step of Encryption**: The `CipherTextGenerator` transforms the encrypted matrix data into a Base64 string, marking the end of the encryption pipeline.
-   **Transmission and Storage**: Base64 ensures that the encrypted data is safely transmitted over any medium without corruption, as it transforms binary data into a printable string format.

Integration with Matrix Operations
----------------------------------

-   **Matrix to Byte Array**: After encryption, the 3D matrix is flattened into a 1D byte array.
-   **Byte Array to Base64**: The byte array is then Base64 encoded, which transforms it into a format suitable for transfer or storage.


