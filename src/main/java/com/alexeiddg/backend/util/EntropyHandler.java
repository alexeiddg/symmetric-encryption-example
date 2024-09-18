package com.alexeiddg.backend.util;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class EntropyHandler {

    public byte[] getEntropy() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[16];
        secureRandom.nextBytes(entropy);
        // TODO: Delete this line
        System.out.println("Entropy: " + Arrays.toString(entropy));
        return entropy;
    }
}