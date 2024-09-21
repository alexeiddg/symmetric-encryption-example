package com.alexeiddg.backend.decryption.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DecryptionRequest {
    String cipherText;
    String clientKey;
}
