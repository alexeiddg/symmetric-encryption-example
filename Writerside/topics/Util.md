# Util - Introduction

Overview
--------

The **Utility Folder** contains essential helper services that support the core encryption and decryption workflow. These services include operations for handling entropy, converting data formats, and managing cryptographic transformations such as hashing. Each utility plays a vital role in ensuring the overall encryption process is secure, efficient, and flexible.

The primary components in the utility folder are:

1.  **EntropyHandler**: A service that provides cryptographically secure entropy, used for generating unpredictable and secure keys.
2.  **StringToBitstream**: A class responsible for converting data between different formats, specifically handling matrix transformations and padding.
3.  **HashService**: A service that applies secure cryptographic hashing to keys, ensuring that keys evolve securely over multiple rounds of encryption.

Each of these utilities contributes to the encryption process by preparing, transforming, or securing data in various stages of encryption and decryption.

Key Components
--------------

### 1\. **EntropyHandler**

The **EntropyHandler** is responsible for generating cryptographically secure random values (entropy). It uses Java's `SecureRandom` class to produce 16 bytes of random data, which is used to generate encryption keys.

-   **Role**: Provides randomness and unpredictability in key generation.
-   **Why it's important**: Secure entropy is a cornerstone of cryptographic security. Using `SecureRandom` ensures that keys are generated with high-quality randomness, making it extremely difficult for an attacker to predict or reproduce the key.

### 2\. **StringToBitstream**

The **StringToBitstream** class handles data conversion, specifically managing the transformation of byte arrays into 4x4 matrices and vice versa. It also implements **ISO/IEC 7816-4 padding**, a padding scheme used to ensure that the plaintext fits perfectly into the required matrix structure.

-   **Matrix Operations**: This utility converts byte arrays into 4x4 matrices, which are the backbone of the encryption algorithm's matrix-based transformations (such as XOR, transposition, and S-Box substitutions).
-   **Padding**: The **ISO/IEC 7816-4** padding scheme is used to pad the data before encryption, ensuring that the length of the plaintext is a multiple of the matrix block size (16 bytes). After encryption, this padding is removed during decryption to restore the original plaintext.
-   **Role in Encryption/Decryption**:
    -   During encryption, the class converts plaintext into matrix chunks, applies padding, and prepares the data for matrix-based operations.
    -   During decryption, it reconstructs the original byte array from the matrix chunks and removes the padding.

### 3\. **HashService**

The **HashService** applies cryptographic hashing to encryption keys, ensuring that keys evolve securely after each round of encryption. Specifically, it uses the **SHA-256** hashing algorithm to transform the key into a new, secure key for subsequent rounds of encryption or decryption.

-   **Key Hashing**: The **HashService** takes a **KeyMatrix**, flattens it into a byte array, and applies the **SHA-256** hash function. The resulting 32-byte hash is truncated to 16 bytes (to match the size of a 4x4 matrix), and the service rebuilds the matrix from this truncated hash.
-   **Role in Encryption**: After each round of encryption, the key matrix is hashed to create a new, unique key for the next round. This ensures that even if an attacker knows the key for one round, they cannot easily deduce the key for subsequent rounds.
-   **Security Enhancement**: By evolving the key across multiple rounds, the **HashService** introduces a level of security that makes the encryption process significantly harder to break.

Purpose in the Encryption Workflow
----------------------------------

These utilities provide foundational support throughout the encryption and decryption process. Here's how they fit into the overall workflow:

-   **EntropyHandler**: Generates the secure entropy needed to create unpredictable encryption keys.
-   **StringToBitstream**: Converts plaintext into matrix format, applies necessary padding, and facilitates the data transformations required for encryption and decryption.
-   **HashService**: Securely evolves the key between rounds of encryption by applying cryptographic hashing, ensuring that the encryption process is dynamic and difficult to reverse-engineer.

Security Considerations
-----------------------

-   **Entropy**: High-quality entropy from the **EntropyHandler** ensures that the keys are as random and secure as possible.
-   **Padding**: The **ISO/IEC 7816-4** padding implemented in **StringToBitstream** ensures that the data blocks are aligned properly for matrix operations, while also allowing the original data to be recovered during decryption.
-   **Hashing**: The use of **SHA-256** hashing in the **HashService** ensures that keys evolve in a cryptographically secure manner, making it exceedingly difficult to predict or replicate keys across encryption rounds.

