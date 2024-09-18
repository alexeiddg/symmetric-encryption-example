The keygen service follows the workflow below:
1. A Key is requested from the encryption service
2. The encryption service sends the entropy to the keygen service
3. The keygen service normalizes the entropy
4. The keygen service hashes the entropy
5. The keygen service creates a key matrix
6. The keygen service calls the key stretching service
7. The key Stretching service embeds the number of rounds for the encryption process in the first byte
8. the keygen service returns the key matrix to the encryption service
9. the key is returned to the client as a string byte array encoded to HEX