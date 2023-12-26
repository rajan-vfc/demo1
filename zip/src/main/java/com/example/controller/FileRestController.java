package com.example.controller;

import com.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileRestController {

    private final FileService fileService;

    @Autowired
    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/encrypt")
    public ResponseEntity<String> encryptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam(value = "outputFileName", required = false) String outputFileName
    ) {
        try {
            fileService.encryptAndSave(file, password, outputFileName);
            return ResponseEntity.ok("File encrypted and saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error encrypting file: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam(value = "outputFileName", required = false) String outputFileName
    ) {
        try {
            fileService.decryptAndSave(file, password, outputFileName);
            return ResponseEntity.ok("File decrypted and saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error decrypting file: " + e.getMessage());
        }
    }
}
