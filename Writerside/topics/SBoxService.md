# Confusion: SBoxService

Overview
--------

The `SBoxService` is a key component of the encryption process that deals with the creation and application of S-Boxes (Substitution Boxes). S-Boxes are used to perform non-linear substitutions, providing diffusion and complexity to the encryption algorithm. This service is responsible for generating the S-Box from the key, applying it to a matrix (the core data structure of the encryption process), and generating its inverse for use in decryption.

Purpose and Role in the Encryption Service
------------------------------------------

-   **S-Box Generation**: It creates an S-Box using the encryption key, which will be used to substitute bytes in the matrix, making the encryption process more secure by introducing non-linearity.
-   **Substitution in the Matrix**: The S-Box is applied to each byte in the matrix, replacing each byte with a corresponding value from the S-Box.
-   **Inverse S-Box**: The inverse S-Box is generated for decryption, ensuring that the substitution performed during encryption can be reversed.

Key Concepts
------------

-   **S-Box**: A substitution box is a lookup table used in encryption algorithms to replace byte values with other values. It ensures that even small changes in the input result in significant changes in the output, enhancing security.
-   **SHA-256 Hashing**: Used to seed the randomization of the S-Box with a hash of the key, ensuring that each key generates a unique S-Box.
-   **Matrix-Based Encryption**: The matrix (often 4x4) is a key data structure in the encryption process. This matrix undergoes transformations such as substitution, transposition, and XOR during encryption.
-   **SecureRandom**: A cryptographically secure random number generator used to randomize the S-Box.

* * * * *

Methods
-------

### `generateSBox(byte[] key)`

**Objective**: Generate a unique S-Box based on the given encryption key using SHA-256 hashing and a secure random generator.

```
    public static byte[] generateSBox(byte[] key) throws NoSuchAlgorithmException {
        byte[] sBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            sBox[i] = (byte) i;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] keyHash = md.digest(key);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(keyHash);

        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);
            byte temp = sBox[i];
            sBox[i] = sBox[j];
            sBox[j] = temp;
        }

        return sBox;
    }
```

### Process: {id="process_1"}

1.  **Initialize S-Box**:

    -   An array of 256 bytes is initialized, with values ranging from 0 to 255.
    -   This array will serve as the initial state of the S-Box.
2.  **Hash the Key**:

    -   The provided key is hashed using the SHA-256 algorithm to produce a 32-byte hash.
    -   This hash will be used as the seed for randomizing the S-Box.
3.  **Seed SecureRandom**:

    -   A `SecureRandom` instance using the SHA1PRNG algorithm is initialized.
    -   The SHA-256 hash of the key is used as the seed for the random number generator. This ensures that each key produces a unique random sequence, and thus a unique S-Box.
    - "SHA1PRNG" is the name of a pseudo random number generator (the PRNG in the name). That means that it uses the SHA1 hash function to generate a stream of random numbers. SHA1PRNG is a proprietary mechanism introduced by Sun at the time. -From stackoverflow
4.  **Randomize S-Box**:

    -   A Fisher-Yates shuffle is performed on the S-Box array. This shuffle ensures that the S-Box values are randomized but consistently derived from the provided key.
    -   The shuffle works by iterating backward through the S-Box, randomly swapping the current byte with a byte at a randomly chosen index.

### Result: {id="result_1"}

-   **Unique S-Box**: The method returns a fully shuffled 256-byte S-Box, where each byte maps to a different value.

* * * * *

### `applySBoxSubstitution(byte[][][] matrixChunks, byte[] sBox)`

**Objective**: Apply the S-Box to each byte in a matrix, performing a non-linear substitution that increases the diffusion of the encryption.

```
    public byte[][][] applySBoxSubstitution(byte[][][] matrixChunks, byte[] sBox) {
        for (byte[][] matrix : matrixChunks) {
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    int byteValue = Byte.toUnsignedInt(matrix[row][col]);
                    matrix[row][col] = sBox[byteValue];
                }
            }
        }
        return matrixChunks;
    }
```

### Process: {id="process_2"}

1.  **Iterate Over Matrix Chunks**:

    -   The method takes a 3D matrix (`matrixChunks`) representing chunks of data (each chunk being a 4x4 matrix).
    -   It iterates over each 4x4 matrix and over each byte within the matrix.
2.  **Substitute Each Byte**:

    -   For each byte in the matrix, its value is used as an index in the S-Box.
    -   The byte in the matrix is then replaced with the value from the S-Box at the corresponding index.

### Matrix Operations:

-   **Matrix Transformation**:
    -   The 4x4 matrix undergoes a transformation where each byte is replaced by the corresponding byte in the S-Box.
    -   This substitution significantly changes the matrix's content, making it difficult to reverse-engineer without the correct S-Box.For example, if a byte in the matrix has a value of 42, it is replaced by the value located at index 42 in the S-Box.

1.  **Repeat for All Chunks**:
    -   The process is repeated for all chunks (4x4 matrices) within the 3D matrix.

### Result: {id="result_2"}

-   **Transformed Matrix**: The method returns the transformed matrix, where each byte has been substituted using the S-Box. This adds non-linearity and diffusion to the encryption, making the output more resistant to analysis.

#### Before S-Box Substitution:

Matrix:
`| 42  | 75  | 23  | 89  |
| 100 | 34  | 200 | 17  |
| 50  | 99  | 240 | 128 |
| 11  | 88  | 73  | 44  |`

#### After S-Box Substitution:

Matrix:
`| SBox[42] | SBox[75]  | SBox[23]  | SBox[89]  |
| SBox[100] | SBox[34] | SBox[200] | SBox[17] |
| SBox[50]  | SBox[99] | SBox[240] | SBox[128] |
| SBox[11]  | SBox[88] | SBox[73]  | SBox[44]  |`

* * * * *

### `generateInverseSBox(byte[] sBox)`

**Objective**: Generate the inverse of the given S-Box to be used during decryption.

```
    public static byte[] generateInverseSBox(byte[] sBox) {
        byte[] inverseSBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            inverseSBox[sBox[i] & 0xFF] = (byte) i;
        }
        return inverseSBox;
    }
```

### Process:

1.  **Initialize Inverse S-Box**:

    -   An array of 256 bytes is created to store the inverse S-Box.
2.  **Inverse Mapping**:

    -   The method iterates over the original S-Box.
    -   For each byte in the S-Box, it sets the inverse S-Box at the index of the original byte to the current index.

    Example: If `sBox[42] = 15`, then `inverseSBox[15] = 42`.

### Result:

-   **Inverse S-Box**: A byte array where each value maps back to its original value in the S-Box. This allows the decryption process to reverse the substitution performed during encryption.

* * * * *

Role in the Encryption Workflow
-------------------------------

-   **During Encryption**:

    -   The `generateSBox` method is used to create a unique S-Box based on the encryption key. The `applySBoxSubstitution` method is used to substitute each byte in the matrix, increasing the complexity and security of the encrypted data.
-   **During Decryption**:

    -   The `generateInverseSBox` method creates the inverse S-Box, which is applied to the matrix during decryption to reverse the substitutions made during encryption.

* * * * *

Matrix Operations Explained
---------------------------

-   **Matrix Substitution**:

    -   The matrix, typically represented as 4x4 chunks of data, is transformed by substituting each byte with a value from the S-Box. This process introduces non-linearity, meaning that small changes in the input can result in large, unpredictable changes in the output. This is critical for creating secure, unpredictable ciphertext.
-   **Byte Substitution**:

    -   Each byte in the matrix is treated as an index into the S-Box, where it is replaced by the value at that index. This means that after substitution, the matrix's original values are completely altered, making it difficult to trace back to the original values without the correct S-Box.

* * * * *

Integration in the Encryption Service
-------------------------------------

-   **S-Box Substitution**:

    -   The `SBoxService` applies byte-level substitution during encryption, which provides diffusion and enhances security.
    -   During decryption, the inverse S-Box is applied to reverse the substitutions and retrieve the original data.
-   **Key-Dependent**:

    -   The S-Box is generated based on the encryption key, ensuring that each encryption key produces a unique S-Box, making the encryption more secure.