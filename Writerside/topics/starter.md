Overview of Encryption/Decryption Spring Boot Application
=========================================================

Introduction
------------

This Spring Boot application implements a unique experimental symmetric encryption algorithm. The main goal is to provide an encryption and decryption mechanism that involves multiple cryptographic transformations, including S-Box substitutions, XOR operations, bit shifts, matrix transpositions, and cipher block chaining (CBC). The approach aims to explore custom encryption techniques while enhancing security by dynamically evolving keys through hashing.

We use POST mapping over other REST mappings like GET because encryption and decryption operations require the transmission of sensitive data (plaintext and ciphertext). POST is designed for sending data in the body of the request, which ensures that sensitive information is not exposed in the URL, as would be the case with a GET request. Additionally, POST is used for actions that modify server-side resources or involve processing that is not idempotent (i.e., the same operation may result in different outcomes), such as encryption. This makes POST the ideal choice for handling encryption and decryption processes in a secure and scalable way.

In this application, you will find two main endpoints:

-   **/encrypt**: This endpoint receives plaintext data and returns an encrypted ciphertext, along with the encryption key and the number of rounds used.
-   **/decrypt**: This endpoint takes the encrypted ciphertext and the client key and reverses the encryption process, returning the original plaintext.

Both of these endpoints are exposed through **REST APIs** that can be accessed by making HTTP POST requests.

Spring Boot Integration
-----------------------

The application is built using the **Spring Boot** framework, which simplifies the creation of RESTful APIs by providing out-of-the-box components for dependency injection, request handling, and response management. In the case of this project, Spring Boot helps expose the encryption and decryption functionalities via REST endpoints.

### Key Components

-   **`EncryptionController`**: Handles incoming requests for encryption, receives plaintext data, and returns the corresponding encrypted response.
-   **`DecryptionController`**: Manages decryption requests, taking the encrypted data and key, and returning the original plaintext.
-   **`EncryptionService` & `DecryptionService`**: The core services responsible for applying the encryption and decryption algorithms.
-   **Request Mappings**: The **@RequestMapping** annotation maps incoming API requests to the respective methods, `/encrypt` and `/decrypt`.

Encryption and Decryption Controllers
-------------------------------------

### `EncryptionController`

```
@RestController
@RequestMapping("/api")
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public ResponseEntity<EncryptionResponse> thing(@RequestBody EncryptionRequest request) throws NoSuchAlgorithmException {
        EncryptionResponse response = encryptionService.encrypt(request.getText());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
```

-   **Endpoint**: `/api/encrypt`
-   **Method**: POST
-   **Functionality**: Accepts a JSON payload containing plaintext, encrypts it using the custom symmetric algorithm, and returns a JSON response with the ciphertext, encryption key, and number of encryption rounds.
-   **Flow**:
    1.  The `encrypt` method is triggered when a POST request is made to `/api/encrypt`.
    2.  The method uses the **`EncryptionService`** to perform the encryption.
    3.  The response contains the encrypted data and metadata required for decryption.

### `DecryptionController`

```
@RestController
@RequestMapping("/api")
public class DecryptionController {

    @Autowired
    private DecryptionService keyDecryptionService;

    @PostMapping("/decrypt")
    public ResponseEntity<String> thing(@RequestBody DecryptionRequest request) throws Exception {
        String response = keyDecryptionService.decrypt(request.getClientKey(), request.getCipherText());
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response);
    }
}
```

-   **Endpoint**: `/api/decrypt`
-   **Method**: POST
-   **Functionality**: Accepts a JSON payload with an encrypted ciphertext and a key, performs the decryption process, and returns the original plaintext.
-   **Flow**:
    1.  The `decrypt` method is called when a POST request is made to `/api/decrypt`.
    2.  It uses the **`DecryptionService`** to apply the decryption algorithm.
    3.  The response contains the plaintext result in plain text format.

Symmetric Encryption Algorithm
------------------------------

The algorithm used in this application is a symmetric encryption method, meaning the same key is used for both encryption and decryption. The encryption involves multiple rounds of transformations, with each round adding more complexity to the ciphertext, ensuring security and confidentiality.

### Key Features of the Algorithm

-   **S-Box Substitution**: A non-linear transformation used to obscure the relationship between the plaintext and ciphertext.
-   **XOR Operations**: Ensures confusion by mixing the key with the plaintext/ciphertext matrix.
-   **Bit Shifting and Transposition**: These transformations increase diffusion by spreading the influence of individual bytes across the matrix.
-   **Cipher Block Chaining (CBC)**: Ensures that each block of plaintext is dependent on the previous block, adding an additional layer of security.

The algorithm also evolves the key after each round using **SHA-256 hashing**. This ensures that each round uses a slightly different key, further complicating any attempt at cryptanalysis.

----------

This Spring Boot application is an exploration of a unique symmetric encryption scheme. By providing a RESTful API for both encryption and decryption, it allows developers to experiment with and understand the intricacies of custom encryption techniques. The combination of matrix-based transformations and cryptographic primitives, such as CBC and S-Box, makes this algorithm a valuable learning tool for understanding the underlying mechanics of symmetric encryption.

This setup is ideal for testing, understanding, and improving upon cryptographic algorithms in a modern microservice architecture using Spring Boot.

