package com.alexeiddg.backend.util;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EntropyHandler {

    public byte[] getEntropy() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[16];
        secureRandom.nextBytes(entropy);
        return entropy;
    }
}