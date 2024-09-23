package com.alexeiddg.backend.decryption.controller;

import com.alexeiddg.backend.decryption.model.DecryptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alexeiddg.backend.decryption.service.DecryptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
