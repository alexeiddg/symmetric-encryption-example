package com.alexeiddg.backend.encryption.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EncryptionRequest {
    private String text;
}
