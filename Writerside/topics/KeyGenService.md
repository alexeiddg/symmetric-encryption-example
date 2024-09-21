# KeyGenService

Overview
--------

The `KeyGenService` class is responsible for generating a 128-bit encryption key based on entropy, and embedding the number of encryption rounds within the key matrix. This class integrates both the `EntropyHandler`, which provides randomness, and the `KeyEmbeddingService`, which stores the number of encryption rounds directly into the key matrix.

Purpose and Role in the Encryption Service
------------------------------------------

-   **Key Generation from Entropy**: The service generates a secure key using random entropy provided by the `EntropyHandler`. The key is formatted into a 4x4 byte matrix, which is essential for encryption operations.
-   **Embedding Rounds in the Key**: The number of encryption rounds is embedded into the first byte of the key matrix, ensuring that encryption and decryption processes apply the correct number of rounds.
-   **Core Key Provider**: The class plays a central role in generating the key for encryption services, ensuring that both entropy (for security) and round control (for transformation consistency) are handled seamlessly.

Key Concepts
------------

-   **Entropy**: Random data generated using `SecureRandom` to ensure cryptographic security. In this case, 16 bytes of entropy are used to create a 128-bit encryption key.
-   **KeyMatrix**: A 4x4 byte matrix that represents the encryption key. The matrix is generated using the entropy and has the number of rounds embedded in the first byte.
-   **Embedding Rounds**: The process of storing the number of encryption rounds in the first byte of the key matrix, ensuring synchronization between encryption and decryption.

### `generateKeyFromEntropy()`

**Objective**: Generate a 4x4 byte `KeyMatrix` from entropy data.

```
    public KeyMatrix generateKeyFromEntropy() {
        assert entropyHandler != null;
        byte[] normalizedEntropy = entropyHandler.getEntropy();

        if (normalizedEntropy.length != 16) {
            throw new IllegalArgumentException("Entropy must be 16 bytes long");
        }

        byte[][] matrix = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(normalizedEntropy, i * 4, matrix[i], 0, 4);
        }

        return new KeyMatrix(matrix);
    }
```

### Process: {id="process_1"}

1.  **Fetch Entropy**:
    -   The method calls `entropyHandler.getEntropy()` to retrieve a 16-byte array of random data (entropy) that will be used to generate the key matrix.
2.  **Validate Entropy Length**:
    -   The length of the entropy is checked to ensure it is exactly 16 bytes (128 bits), as this is the required size for the key. If the length is not 16 bytes, an exception is thrown.
3.  **Construct Key Matrix**:
    -   A 4x4 matrix is created to store the key.
    -   The method iterates through the entropy, copying 4-byte chunks into each row of the matrix, thereby constructing the `KeyMatrix`.
4.  **Return KeyMatrix**:
    -   The generated matrix is returned as a `KeyMatrix` object, representing the newly created encryption key.

### Result: {id="result_1"}

-   **128-bit Key**: A secure 4x4 matrix (128-bit) key generated from entropy, ready to be used in encryption or further processed to embed the number of rounds.

### `embedKey(KeyMatrix entropyKey)`

**Objective**: Embed the number of encryption rounds into the first byte of a `KeyMatrix`.

```
    public KeyMatrix embedKey(KeyMatrix entropyKey) {
        return keyEmbeddingService.embedKey(entropyKey);
    }
```

### Process: {id="process_2"}

1.  **Call KeyEmbeddingService**:
    -   The method delegates the task of embedding the number of rounds to the `KeyEmbeddingService`.
2.  **Return Embedded Key**:
    -   The key returned from `KeyEmbeddingService` now has the number of encryption rounds stored in its first byte, ensuring consistency across the encryption and decryption process.

### Result: {id="result_2"}

-   **Key with Rounds Embedded**: The modified `KeyMatrix` has the number of encryption rounds stored in the first byte and is ready for use in encryption.

### `generateKey()`

**Objective**: Generate a complete encryption key by creating a key from entropy and embedding the number of rounds.

```
    public KeyMatrix generateKey() {
        KeyMatrix key = generateKeyFromEntropy();
        return embedKey(key);
    }
```

### Process:

1.  **Generate Entropy-based Key**:
    -   The method first calls `generateKeyFromEntropy()` to produce a 4x4 matrix from random entropy.
2.  **Embed Rounds into Key**:
    -   The method calls `embedKey()` to store the number of encryption rounds in the first byte of the key matrix.
3.  **Return Final Key**:
    -   The fully formed key, which now contains the encryption rounds in its first byte, is returned for use in encryption.

### Result:

-   **Final KeyMatrix**: A key matrix that is both entropy-based and contains the necessary round information for encryption.

Role in the Encryption Workflow
-------------------------------

-   **Key Generation**:
    -   `KeyGenService` provides the encryption key that drives the encryption and decryption processes.
    -   The key is generated from random entropy to ensure cryptographic security.
-   **Round Control**:
    -   By embedding the number of encryption rounds into the first byte, the service ensures that both encryption and decryption use the correct number of rounds. This is crucial for ensuring consistency and security in the encryption workflow.

Matrix Operations Explained
---------------------------

-   **Matrix Construction**:
    -   The 16-byte entropy is split into 4-byte chunks and arranged into a 4x4 matrix. This matrix is used in encryption to perform matrix-based transformations (such as XOR, transposition, etc.).
-   **Matrix Embedding**:
    -   The number of encryption rounds is embedded in the first byte (`matrix[0][0]`) of the matrix. This ensures that encryption and decryption remain synchronized with the correct number of transformation rounds.

Integration in Encryption Service
---------------------------------

-   **Key Provider**:
    -   `KeyGenService` is the core provider of encryption keys for the entire encryption service. Without this, the encryption and decryption processes would not have a secure key to operate on.
-   **Secure and Random**:
    -   By leveraging entropy from `SecureRandom`, the service ensures that every generated key is unique and secure. This significantly increases the security of the encryption algorithm.