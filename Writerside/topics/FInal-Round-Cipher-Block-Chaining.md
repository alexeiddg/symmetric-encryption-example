# FInal Round: Cipher Block Chaining

Overview
--------

The `CBCService` (Cipher Block Chaining) class is a vital component in the encryption process that operates in the last round of the encryption service. It ensures that each block of plaintext or ciphertext is linked with the previous one through XOR operations. This introduces dependency between blocks, enhancing the security of the encryption by adding **diffusion**.

```
    public byte[][][] applyCBC(byte[][][] plaintextBlocks, KeyMatrix finalHashedKey) {
        byte[][][] resultBlocks = new byte[plaintextBlocks.length][4][4];
        byte[] finalKeyAsIV = finalHashedKey.flattenMatrix();
        byte[][] ivMatrix = KeyMatrix.rebuildMatrix(finalKeyAsIV).matrix();

        resultBlocks[0] = xorBlock(plaintextBlocks[0], ivMatrix);

        for (int i = 1; i < plaintextBlocks.length; i++) {
            resultBlocks[i] = xorBlock(plaintextBlocks[i], resultBlocks[i - 1]);
        }

        return resultBlocks;
    }

    private byte[][] xorBlock(byte[][] block1, byte[][] block2) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) (block1[row][col] ^ block2[row][col]);
            }
        }
        return result;
    }
```

Cipher Block Chaining Explained
-------------------------------

-   **Cipher Block Chaining (CBC)**: This mode ensures that the encryption of each plaintext block depends on both the plaintext block and the ciphertext of the previous block (or an initialization vector for the first block). It enhances diffusion, meaning that a small change in plaintext affects all subsequent ciphertext blocks.

-   **Diffusion**: In this context, diffusion ensures that small changes in the input (plaintext or key) will produce widespread changes in the output (ciphertext). This makes the encryption more secure.

Key Concepts
------------

-   **Block XORing**: XOR (exclusive OR) is used to combine two blocks, either plaintext with an initialization vector (IV) or ciphertext with the current plaintext block. It is a bitwise operation that ensures the chaining effect of CBC mode.

-   **IV (Initialization Vector)**: The first block of plaintext is XORed with an IV, which is derived from the `finalHashedKey`. This ensures that even if identical plaintext blocks are encrypted multiple times, the resulting ciphertext will be different.

-   **KeyMatrix**: A custom 4x4 matrix (16 bytes) used throughout the encryption service, with the flattened version being used as the IV.

Methods
-------

### `applyCBC`

#### Objective {id="objective_1"}

To apply Cipher Block Chaining to the given plaintext blocks. The result is a sequence of ciphertext blocks, where each block is influenced by the previous ciphertext block.

```
    public byte[][][] applyCBC(byte[][][] plaintextBlocks, KeyMatrix finalHashedKey) {
        byte[][][] resultBlocks = new byte[plaintextBlocks.length][4][4];
        byte[] finalKeyAsIV = finalHashedKey.flattenMatrix();
        byte[][] ivMatrix = KeyMatrix.rebuildMatrix(finalKeyAsIV).matrix();

        resultBlocks[0] = xorBlock(plaintextBlocks[0], ivMatrix);

        for (int i = 1; i < plaintextBlocks.length; i++) {
            resultBlocks[i] = xorBlock(plaintextBlocks[i], resultBlocks[i - 1]);
        }

        return resultBlocks;
    }
```
    
#### Process {id="process_1"}

1.  **Initialization Vector (IV)**:

    -   The `finalHashedKey` is flattened into a 16-byte array, which serves as the IV.
    -   This IV is used for XORing the first block of plaintext.
2.  **First Block XOR**:

    -   The first plaintext block is XORed with the IV to produce the first ciphertext block.
    -   Matrix operation:

        `resultBlocks[0] = plaintextBlocks[0] XOR ivMatrix`

3.  **Subsequent Block XOR**:

    -   Each subsequent plaintext block is XORed with the previous ciphertext block, producing the next ciphertext block.
    -   Matrix operation:

        `resultBlocks[i] = plaintextBlocks[i] XOR resultBlocks[i - 1]`

4.  **Result**:

    -   Returns a list of ciphertext blocks, each dependent on the previous block.

#### Matrix Operations {id="matrix-operations_1"}

-   For each block, the corresponding bytes in the matrix are XORed:

    `result[row][col] = plaintextBlock[row][col] XOR previousBlock[row][col]`

    This ensures that the matrix is altered based on both the current block and the previous ciphertext block, creating a chaining effect.

### `reverseCBC`

#### Objective {id="objective_2"}

To reverse the Cipher Block Chaining process during decryption. It transforms ciphertext blocks back into plaintext blocks, where each block depends on the previous ciphertext block.

```
    public byte[][][] reverseCBC(byte[][][] ciphertextBlocks, KeyMatrix finalHashedKey) {
        byte[][][] plaintextBlocks = new byte[ciphertextBlocks.length][4][4];
        byte[] finalKeyAsIV = finalHashedKey.flattenMatrix();
        byte[][] ivMatrix = KeyMatrix.rebuildMatrix(finalKeyAsIV).matrix();

        plaintextBlocks[0] = xorBlock(ciphertextBlocks[0], ivMatrix);

        for (int i = 1; i < ciphertextBlocks.length; i++) {
            plaintextBlocks[i] = xorBlock(ciphertextBlocks[i], ciphertextBlocks[i - 1]);
        }

        return plaintextBlocks;
    }
```

#### Process

1.  **IV-Based XOR**:

    -   The first ciphertext block is XORed with the IV (derived from the `finalHashedKey`) to retrieve the first plaintext block.
2.  **Subsequent Block XOR**:

    -   Each subsequent ciphertext block is XORed with the previous ciphertext block to retrieve the corresponding plaintext block.
3.  **Result**:

    -   Returns the plaintext blocks, reconstructing the original message.

#### Matrix Operations

-   For each block:

    `plaintextBlock[row][col] = ciphertextBlock[row][col] XOR previousCiphertextBlock[row][col]`

    This reverses the chaining effect from the encryption phase, retrieving the original plaintext.

### `xorBlock`

#### Objective

To XOR two 4x4 matrices (blocks).

```
    private byte[][] xorBlock(byte[][] block1, byte[][] block2) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) (block1[row][col] ^ block2[row][col]);
            }
        }
        return result;
    }
```

#### Process {id="process_2"}

1. For each byte in the matrix, perform the XOR operation:
    
`result[row][col] = block1[row][col] XOR block2[row][col]`

2. Returns the XORed result matrix.

#### Role in CBC

-   This method is the backbone of the CBC process, as it applies the XOR operation to link plaintext and ciphertext blocks.

Encryption Flow
---------------

The CBC method is applied after all rounds of S-box substitution, XOR, bit shifting, and transposition. It is the final step before generating the ciphertext. The reverse CBC method is applied first during decryption to retrieve the original plaintext blocks.

Diffusion and Confusion
-----------------------

-   **Diffusion**: CBC adds diffusion because changes in one plaintext block influence all subsequent ciphertext blocks. Even a minor change in the input will spread across all blocks, making the encryption more secure.
-   **Confusion**: While CBC primarily focuses on diffusion, it complements other methods (S-box substitution, bit-shifting, and XOR) that introduce confusion. These techniques work together to obscure the relationship between the ciphertext and the key.