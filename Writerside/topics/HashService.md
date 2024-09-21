# HashService

Overview
--------
The `HashService` class is a crucial component of the encryption service. It is responsible for updating the encryption key through hashing, enhancing security by ensuring that each round of encryption uses a different key derived from the previous one.


    public KeyMatrix applySha256ToMatrix(KeyMatrix keyMatrix) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] flattenedKey = keyMatrix.flattenMatrix();
        byte[] hash = digest.digest(flattenedKey);
        byte[] truncatedHash = new byte[16];
        System.arraycopy(hash, 0, truncatedHash, 0, 16);

        return KeyMatrix.rebuildMatrix(truncatedHash);
    }


Purpose and Role in the Encryption Service
------------------------------------------

-   **Key Evolution**: In multi-round encryption algorithms, evolving the key after each round adds an extra layer of security. `HashService` achieves this by applying a cryptographic hash function to the key matrix to produce a new key for subsequent rounds.
-   **Security Enhancement**: By using a cryptographic hash function, the service ensures that keys are difficult to predict or reverse-engineer, even if an attacker gains knowledge of the current key.

Key Concepts
------------

-   **SHA-256 Hash Function**: A widely-used cryptographic hash function that produces a 256-bit (32-byte) hash. It is deterministic, pre-image resistant, and collision-resistant, making it suitable for cryptographic applications.
-   **KeyMatrix**: A custom data structure representing the key as a 4x4 matrix of bytes (16 bytes in total). This matrix format is used throughout the encryption service for matrix-based transformations.
-   **Matrix Flattening and Rebuilding**: The process of converting a 2D matrix into a 1D byte array (flattening) and reconstructing a matrix from a byte array (rebuilding). This is necessary for operations like hashing that require linear data inputs.

### Description

-   **Objective**: To generate a new `KeyMatrix` by applying the SHA-256 hash function to the existing key matrix.
-   **Process**:
    1.  **Flatten the Key Matrix**:
        -   The 4x4 `keyMatrix` is converted into a 1D byte array of length 16.
        -   This prepares the key data for hashing, as the hash function operates on byte arrays.
    2.  **Apply SHA-256 Hashing**:
        -   A `MessageDigest` instance is created with the SHA-256 algorithm.
        - the MessageDigest with SHA-256 ensures that the output (the hash) is always 32 bytes (256 bits), regardless of the size of the input data
        -   The flattened key byte array is passed to the `digest` method, producing a 32-byte hash value.
        - the digest function processes input data (usually a byte array) and returns the corresponding hash.
    3.  **Truncate the Hash**:
        -   Since the `KeyMatrix` requires 16 bytes (for a 4x4 matrix), only the first 16 bytes of the 32-byte hash are used.
        - Truncating the hash means taking only a portion of the full hash output, reducing its size. In the case of truncating a SHA-256 hash (which is 32 bytes), selecting only the first 16 bytes (or half) of the total hash
        -   This truncation ensures the new key fits the required matrix size.
    4.  **Rebuild the Key Matrix**:
        -   The truncated 16-byte hash is used to rebuild a new `KeyMatrix`.
        -   The `KeyMatrix.rebuildMatrix` method reconstructs the 4x4 matrix from the byte array.
-   **Result**:
    -   Returns a new `KeyMatrix` that serves as the updated key for the next encryption or decryption round.

### Role in the Encryption Workflow

-   **During Encryption**:
    -   After each encryption round, `applySha256ToMatrix` is called to update the key.
    -   This evolving key approach adds complexity and security to the encryption process.
-   **During Decryption**:
    -   The same method is used to generate the sequence of keys used during encryption, but in reverse order.
    -   This ensures that decryption accurately reverses the transformations applied during encryption.

Matrix Operations Explained
---------------------------

-   **Flattening the Matrix**:
    -   Converts a 4x4 matrix into a 1D byte array.
    -   Necessary for hashing, as the `MessageDigest` operates on byte arrays.
-   **Rebuilding the Matrix**:
    -   Converts a 1D byte array back into a 4x4 matrix.
    -   Ensures the key maintains the required structure for matrix operations in encryption/decryption.

Integration in Encryption Service
---------------------------------

-   **Key Update Mechanism**:
    -   After each encryption round, the key matrix is updated using `applySha256ToMatrix`.
    -   This ensures that each encryption round uses a unique key derived from the previous key.
-   **Enhancing Security**:
    -   By changing the key each round, it becomes significantly harder for an attacker to decrypt the message without knowing the key sequence.
-   **Consistency in Decryption**:
    -   The decryption process uses the same sequence of hashed keys in reverse order to accurately revert the encryption transformations.