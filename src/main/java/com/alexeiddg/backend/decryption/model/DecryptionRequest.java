package com.alexeiddg.backend.decryption.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DecryptionRequest {
    String cipherText;
    String clientKey;
}
