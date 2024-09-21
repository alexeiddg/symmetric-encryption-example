package com.alexeiddg.backend.encryption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexeiddg.backend.encryption.service.EncryptionService;
import com.alexeiddg.backend.encryption.model.EncryptionResponse;
import com.alexeiddg.backend.encryption.model.EncryptionRequest;

import java.security.NoSuchAlgorithmException;

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
