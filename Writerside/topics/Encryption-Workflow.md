# Encryption Workflow

Overview
--------

The `EncryptionService` class is responsible for orchestrating the entire encryption workflow. It takes plain text input, applies a series of cryptographic transformations (including key generation, substitution, XOR, bit-shifting, matrix transposition, and CBC), and produces the final encrypted ciphertext. This process leverages matrix-based operations to enhance security, ensuring that each step adds layers of confusion and diffusion to the data.

Key Components Involved
-----------------------

-   **Key Generation**: Uses entropy to generate the initial encryption key and embeds the number of rounds into the key matrix.
-   **S-Box Substitution**: Introduces non-linearity by substituting byte values based on a generated S-Box.
-   **XOR Operations**: Adds confusion by XORing the matrix with the key in multiple rounds.
-   **Matrix Transformations**: Performs bit-shifting and matrix transposition, further diffusing the data.
-   **Cipher Block Chaining (CBC)**: Ensures that each block of ciphertext depends on the previous block, adding additional security.
-   **Final Ciphertext Generation**: Converts the transformed matrix into a Base64-encoded string for transmission or storage.

Workflow Explanation
--------------------

### 1\. **Key Generation**

The encryption process begins by generating a key matrix using the `KeyGenService`.

-   **Matrix Format**: The key is represented as a 4x4 matrix, which is used for various transformations during encryption.
-   **Embedding the Number of Rounds**: The first byte of the key matrix determines how many rounds the encryption will run. This value (1--16) is embedded directly in the first byte of the matrix, ensuring dynamic key behavior across rounds.


`KeyMatrix encryptionKey = getEncryptionKey();
byte firstByte = keyMatrix[0][0];
int firstByteAsNumber = Byte.toUnsignedInt(firstByte);`

### 2\. **Text Preparation**

The input plain text is converted into a 3D matrix, where each block of text is represented as a 4x4 matrix. This matrix is padded using ISO/IEC 7816-4 padding to ensure that the text fits into 16-byte blocks.

`byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
byte[][][] xorResult = StringToBitstream.generateMatrixPadding(textBytes);`

### 3\. **S-Box Substitution**

An S-Box (Substitution Box) is generated from the key and applied to the matrix. The S-Box introduces non-linearity into the encryption process, ensuring that identical inputs result in different outputs.

-   **Matrix Transformation**: Each byte in the matrix is substituted with a corresponding value from the S-Box.
-   **Objective**: This step adds confusion to the data, making it difficult to reverse-engineer.


`byte[] keyBytes = encryptionKey.flattenMatrix();
byte[] sBox = SBoxService.generateSBox(keyBytes);
xorResult = sBoxService.applySBoxSubstitution(xorResult, sBox);`

### 4\. **XOR Rounds**

The core encryption happens in multiple rounds (determined by the first byte of the key). During each round:

-   **XOR with Key Matrix**: The matrix is XORed with the current key matrix, further scrambling the data.
-   **Confusion Addition**: The XOR operation ensures that the data in the matrix is obfuscated with each round.

`xorResult = xorService.xorBitstream(xorResult, encryptionKey);`

### 5\. **Bit Shifting and Matrix Transposition**

Each round involves:

-   **Bit Shifting**: The bytes in the matrix are shifted left or right depending on the sum of the row values in the key matrix. This further diffuses the data, spreading small changes across the matrix.

    -   **Encrypting**: Bit shifts are applied to the left.
    -   **Matrix Update**: After shifting, the matrix is updated.
-   **Matrix Transposition**: Rows and columns of the matrix are transposed based on a permutation derived from the key. This reorders the matrix, adding another layer of diffusion to the data.


`for (int j = 0; j < xorResult.length; j++) {
    xorResult[j] = transformService.performBitShift(xorResult[j], encryptionKey, true);
    xorResult[j] = transposeService.transposeMatrix(xorResult[j], encryptionKey);
}`

### 6\. **Key Update (Hashing)**

After each round, the key matrix is hashed using the SHA-256 algorithm. Only the first 16 bytes of the hash are used to create a new key matrix for the next round.

-   **Purpose**: This ensures that each round uses a different key derived from the previous key, enhancing security by making the key unpredictable.

`encryptionKey = hashService.applySha256ToMatrix(encryptionKey);`

### 7\. **Cipher Block Chaining (CBC)**

Cipher Block Chaining (CBC) is applied to link all blocks of the matrix together. Each block is XORed with the previous block, ensuring that a change in one block affects all subsequent blocks.

-   **Initialization Vector (IV)**: The IV is derived from the final hashed key.
-   **Chaining Effect**: Each block of plaintext is XORed with the previous block's ciphertext to generate the current ciphertext.

`xorResult = cbcService.applyCBC(xorResult, encryptionKey);`

### 8\. **Final Ciphertext Generation**

The final matrix, after all transformations, is flattened into a byte array and encoded into a Base64 string. This ciphertext is the final output of the encryption process, ready for transmission or storage.

`String cipherText = cipherTextGenerator.returnCipherText(xorResult);
return new EncryptionResponse(cipherText, clientKey, firstByteAsNumber);`

Summary of Matrix Operations
----------------------------

-   **Initial Matrix Padding**: Plaintext is converted into a 3D matrix with padding.
-   **S-Box Substitution**: Each byte in the matrix is substituted based on the S-Box.
-   **XOR with Key Matrix**: The matrix is XORed with the key matrix in multiple rounds.
-   **Bit Shifting**: Bytes are shifted left or right based on the row values of the key matrix.
-   **Matrix Transposition**: The matrix rows and columns are permuted.
-   **CBC Chaining**: Matrix blocks are XORed with the previous block's ciphertext.

Role of Key Matrix
------------------

The key matrix evolves with each round, ensuring dynamic encryption across rounds:

-   **Initial Key**: Generated from entropy and embedded with the number of rounds.
-   **Updated Key**: After each round, the key is hashed to produce a new matrix.