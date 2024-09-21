# StringToBitstream

Overview
--------

The `StringToBitstream` class handles conversions between byte arrays and matrices for encryption and decryption operations. It includes methods to apply padding to ensure data fits into a 4x4 matrix format and methods to reverse this process. The class plays a crucial role in preparing data for matrix-based encryption transformations by splitting the byte arrays into matrix chunks and padding the data as necessary.

In this encryption service, matrix-based transformations are applied to the data at various stages, and padding is critical for ensuring that the data can be cleanly divided into blocks of the correct size.

Purpose and Role in the Encryption Service
------------------------------------------

-   **Matrix Conversion**: This class ensures that the plaintext and ciphertext are divided into matrix chunks, which allows for matrix-based cryptographic operations.
-   **Padding**: Padding is applied to ensure that data fits perfectly into the 4x4 matrix blocks. The ISO/IEC 7816-4 padding standard is used to pad the data to the correct block size during encryption and is removed during decryption.

### Key Concepts

-   **ISO/IEC 7816-4 Padding**: This padding scheme is used in cryptography to ensure that the data is the correct size for encryption. It adds a `0x80` byte after the actual data, followed by `0x00` bytes to fill the remaining space in the block. This ensures that each data block is complete and fits into the required matrix size.
-   **Matrix-based Cryptography**: Data is transformed into a 4x4 matrix format, where each matrix represents a chunk of the data. This allows for complex matrix transformations such as transpositions, XOR operations, and S-Box substitutions.
-   **Byte Array to Matrix Transformation**: Converts a byte array into a matrix format, allowing cryptographic transformations to be performed on the data.

### Functions

### `generateMatrixPadding(byte[] byteArray)`

**Objective**: This function applies ISO/IEC 7816-4 padding to ensure the byte array is of the correct length to fit into matrix blocks of 16 bytes (4x4 matrix).

```
public static byte[][][] generateMatrixPadding(byte[] byteArray) {
    int chunkSize = 16;
    int paddingLength = chunkSize - (byteArray.length % chunkSize);
    int totalLength = byteArray.length + paddingLength;
    byte[] paddedByteArray = Arrays.copyOf(byteArray, totalLength);

    paddedByteArray[byteArray.length] = (byte) 0x80;

    int totalChunks = totalLength / chunkSize;
    byte[][][] matrixChunks = new byte[totalChunks][4][4];

    for (int i = 0; i < totalChunks; i++) {
        int start = i * chunkSize;
        byte[] chunk = Arrays.copyOfRange(paddedByteArray, start, start + chunkSize);

        byte[][] matrix = new byte[4][4];
        for (int j = 0; j < chunkSize; j++) {
            matrix[j / 4][j % 4] = chunk[j];
        }

        matrixChunks[i] = matrix;
    }

    return matrixChunks;
}
```

**Process**:

1.  **Padding Length Calculation**:

    -   The byte array is divided into 16-byte chunks.
    -   If the length of the byte array is not a multiple of 16, padding is needed to make it so.
    -   Padding length is calculated as `16 - (byteArray.length % 16)` to determine how many bytes are needed to complete the block.
2.  **Applying ISO/IEC 7816-4 Padding**:

    -   A `0x80` byte is added immediately after the data.
    -   The remaining bytes are padded with `0x00` to fill the block to 16 bytes.
    -   This ensures that even if the last block contains less than 16 bytes of data, it will be filled correctly for encryption.
3.  **Matrix Conversion**:

    -   The padded byte array is split into 16-byte chunks.
    -   Each 16-byte chunk is then reshaped into a 4x4 matrix.
    -   Each matrix chunk represents a part of the data, allowing for the application of encryption operations like XOR, transposition, and S-Box substitution.

**Result**: A 3D matrix (`byte[][][]`) where each matrix chunk represents a 4x4 block of the original padded data.

### **Why Use ISO/IEC 7816-4 Padding?**

In the context of your encryption service:

-   **Consistency in Matrix Operations**: Every matrix transformation step in the encryption service operates on a 4x4 matrix (16 bytes). If the last chunk of data is not exactly 16 bytes, padding is necessary to ensure uniform block sizes.
-   **Padding Removability**: ISO/IEC 7816-4 padding is easily reversible, as the padding scheme uses a specific byte (`0x80`) to indicate the end of the original data, followed by `0x00` padding bytes. This makes it easy to detect and remove padding during decryption.
-   **Secure and Standardized**: ISO/IEC 7816-4 is a well-recognized padding standard in cryptography, ensuring that the encrypted data remains compatible with cryptographic protocols that expect standardized padding.

* * * * *

### `byteArrayToMatrixChunks(byte[] byteArray)`

**Objective**: Converts a byte array into 4x4 matrix chunks for cryptographic processing.

```
    public static byte[][][] byteArrayToMatrixChunks(byte[] byteArray) {
        int chunkSize = 16;
        int totalLength = byteArray.length;
        int totalChunks = (int) Math.ceil((double) totalLength / chunkSize);
        byte[][][] matrixChunks = new byte[totalChunks][4][4];

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, totalLength);
            byte[] chunk = Arrays.copyOfRange(byteArray, start, end);

            byte[][] matrix = new byte[4][4];
            for (int j = 0; j < chunk.length; j++) {
                matrix[j / 4][j % 4] = chunk[j];
            }

            for (int j = chunk.length; j < chunkSize; j++) {
                matrix[j / 4][j % 4] = 0;
            }

            matrixChunks[i] = matrix;
        }

        return matrixChunks;
    }
```

**Process**:

1.  **Determine Total Chunks**:

    -   The byte array is split into chunks of 16 bytes.
    -   The number of chunks is calculated as the total byte array length divided by 16.
    -   Each chunk will be converted into a 4x4 matrix.
2.  **Matrix Conversion**:

    -   For each chunk of data, a 4x4 matrix is created where each row of the matrix holds 4 bytes.
    -   If the chunk is smaller than 16 bytes (at the end of the byte array), the remaining values are padded with `0x00` to complete the 16-byte block.

**Result**: A 3D matrix (`byte[][][]`) where each 4x4 matrix represents a chunk of the original byte array.

### Matrix Operations:

-   **Matrix Chunking**: Converts the data into a series of matrices, making it easier to apply matrix-based cryptographic operations (such as transposition or XOR).
-   **Padding the Last Chunk**: If the data doesn't divide evenly into 16 bytes, the last chunk is padded with `0x00` to ensure all blocks are 16 bytes.

* * * * *

### `matrixChunksToByteArray(byte[][][] matrixChunks)`

**Objective**: Converts the 4x4 matrix chunks back into a single byte array and removes the ISO/IEC 7816-4 padding.

```
   public static byte[] matrixChunksToByteArray(byte[][][] matrixChunks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[][] matrix : matrixChunks) {
            for (byte[] row : matrix) {
                outputStream.write(row, 0, row.length);
            }
        }

        byte[] fullByteArray = outputStream.toByteArray();

        // Remove ISO/IEC 7816-4 padding
        int i = fullByteArray.length - 1;
        while (i >= 0 && fullByteArray[i] == 0x00) {
            i--;
        }
        if (i >= 0 && fullByteArray[i] == (byte) 0x80) {
            return Arrays.copyOfRange(fullByteArray, 0, i);
        } else {
            throw new IllegalArgumentException("Invalid padding");
        }
    }
```

**Process**:

1.  **Flattening the Matrix**:

    -   The function iterates over each 4x4 matrix chunk and flattens it into a 1D byte array.
    -   All the matrix chunks are combined into a single byte array.
2.  **Removing ISO/IEC 7816-4 Padding**:

    -   After flattening the matrices, the function detects and removes the ISO/IEC 7816-4 padding.
    -   Starting from the end of the byte array, it looks for `0x00` padding bytes until it finds the `0x80` byte, which indicates the end of the actual data.
    -   The padding is then removed, leaving only the original data.

**Result**: The original byte array before padding was applied.

* * * * *

### Role in the Encryption Workflow

-   **Data Preparation for Encryption**:

    -   `generateMatrixPadding()` ensures that the data is properly formatted into 4x4 matrix blocks, which are essential for applying encryption transformations like XOR, S-Box substitution, and transposition.
    -   By applying ISO/IEC 7816-4 padding, the service ensures that the data can be divided into blocks that the encryption algorithm can process.
-   **Matrix Operations**:

    -   The matrix operations in this class allow the byte array to be manipulated and transformed in a way that aligns with matrix-based cryptographic techniques, ensuring the cryptographic operations can be applied uniformly across the data.
-   **Data Reconstruction After Decryption**:

    -   Once decryption has been completed, `matrixChunksToByteArray()` reassembles the data from the matrix chunks and removes the padding.
    -   This function ensures that the original data is restored, making it suitable for use in decryption.

* * * * *

Integration in Encryption Service
---------------------------------

-   **Padding for Encryption**: During encryption, the data is padded and converted into matrix chunks, allowing it to undergo matrix-based cryptographic transformations.
-   **Matrix Representation**: Converting data into a matrix allows for operations like XOR and transposition, enhancing the security of the encryption.
-   **Padding Removal for Decryption**: After decryption, the ISO/IEC 7816-4 padding is removed to restore the original data, ensuring a seamless transition from encrypted to decrypted text.