package com.alexeiddg.backend.encryption.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class EncryptionResponse {
    private String cipherText;
    private String encryptionKey;
    private int iterations;
}
