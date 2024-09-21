# Kegen - Introduction

Overview
--------

The **Key Generation Service** is a core component of the encryption and decryption process within this application. Its primary role is to generate cryptographically secure keys in the form of a **KeyMatrix**. These keys are used in the various transformations and encryption steps of the custom symmetric encryption algorithm.

The service operates in conjunction with the following classes:

1.  **KeyMatrix Model**: A specialized data structure that represents a 128-bit (16-byte) encryption key in a 4x4 matrix format.
2.  **KeyEmbeddingService**: Responsible for embedding the number of encryption rounds in the first byte of the matrix, ensuring that the encryption process can be dynamic.
3.  **KeyGenService**: Combines entropy collection, key generation, and key embedding into a unified service, producing secure keys for encryption.

The entire key generation workflow ensures that each encryption round uses a unique key, enhancing the overall security of the encryption algorithm.

Key Components
--------------

### 1\. **KeyMatrix Model**

The **KeyMatrix** model is a custom representation of a 128-bit key stored as a 4x4 byte matrix. This matrix format is crucial for the matrix-based operations used in the encryption process, such as XOR, transposition, and bit shifts. It provides utility methods for:

-   **Flattening** the matrix into a byte array for linear operations like hashing.
-   **Rebuilding** the matrix from a byte array, ensuring that matrix operations can be easily reverted.

The **KeyMatrix** provides structure to the encryption key, allowing the algorithm to use a matrix form for efficient transformations.

### 2\. **KeyEmbeddingService**

The **KeyEmbeddingService** embeds metadata into the encryption key matrix, specifically encoding the number of encryption rounds into the first byte of the matrix. This ensures that the encryption process is aware of how many rounds of transformations to apply. The embedding adds an additional layer of dynamic control to the encryption process, where each key can define its own number of rounds, making the process less predictable and harder to break.

-   The number of rounds is a randomly generated integer between 1 and 16, which is stored in the first byte of the **KeyMatrix**.
-   This number guides how many times XOR, transformation, and transposition steps are performed.

### 3\. **KeyGenService**

The **KeyGenService** orchestrates the entire key generation process:

1.  **Entropy Collection**: Using the **EntropyHandler** service, the **KeyGenService** gathers 16 bytes of cryptographically secure entropy from a **SecureRandom** instance. This ensures that the keys generated are random and unpredictable.

2.  **Matrix Construction**: The 16 bytes of entropy are converted into a 4x4 byte matrix, creating a **KeyMatrix** that will serve as the base key.

3.  **Key Embedding**: After generating the base **KeyMatrix**, the service calls the **KeyEmbeddingService** to embed the number of encryption rounds into the first byte of the matrix. This completes the creation of the final **KeyMatrix** to be used in the encryption process.

The **KeyGenService** provides the final, prepared key for use in encryption and decryption, ensuring that each encryption cycle uses a secure and properly configured key.

Purpose in the Encryption Workflow
----------------------------------

In the encryption workflow, the **Key Generation Service** plays the following roles:

-   **Initial Key Creation**: It generates the secure key that starts the encryption process.
-   **Dynamic Round Control**: The number of encryption rounds is dynamically determined and embedded into the key matrix, adding unpredictability to the encryption steps.
-   **Matrix Structure**: The key's matrix structure allows it to be directly used in matrix-based transformations during encryption, such as XOR and transposition.

Security Considerations
-----------------------

-   **Randomness and Entropy**: The use of **SecureRandom** ensures that the keys generated are cryptographically secure, preventing predictable key sequences.
-   **Dynamic Round Embedding**: Embedding the number of encryption rounds adds complexity to the algorithm, making it harder for attackers to reverse-engineer the encryption process.
-   **Key Hashing**: Throughout the encryption process, keys are hashed and transformed using the **HashService**, which ensures that the key evolves securely across multiple rounds.

