## Encryption workflow

--- 
## Requesting data
1. The encryption service requests a key from the keygen service
2. The encryption service requests the normalized entropy payload 
3. The encryption service requests the plain text
---
## Creating assets
1. the plain text is converted into a bitstream
2. the bitstream is divided into blocks of 128 bits

---
## Encryption Round 
1. Confusion: Apply S-box substitution based on the key to break plaintext. 
2. Confusion: XOR the result with the key to add further confusion at the bit level. 
3. Diffusion: Perform linear bit shifting based on the key. 
4. Diffusion: transpose the bytes or bits based on the key. 
---
## FInal Round
1. Rotate the bits in each byte or across the entire block by a key-dependent value (e.g., rotating all bits left by n positions).
--- 
## Returning data
1. Return the key as HEX string
2. Return ciphertext as string
3. Return the number of iterations made

---
possible future features:
- MAC generation
- key sharing security 
- add Initialization vector