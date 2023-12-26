package com.example.service;

import com.example.util.FileEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {

    public void encryptAndSave(MultipartFile inputFile, String password, String outputFileName) throws Exception {
        try (InputStream inputStream = inputFile.getInputStream()) {
            String outputDirectory = extractDirectory(inputFile.getOriginalFilename());
            String outputFile = outputDirectory + "/" + (outputFileName != null ? outputFileName : "encrypted_file.enc");
            FileEncryptor.encryptFile(inputStream, outputFile, password);
        } catch (IOException e) {
            throw new Exception("Error encrypting file: " + e.getMessage(), e);
        }
    }

    public void decryptAndSave(MultipartFile inputFile, String password, String outputFileName) throws Exception {
        try (InputStream inputStream = inputFile.getInputStream()) {
            String outputDirectory = extractDirectory(inputFile.getOriginalFilename());
            String outputFile = outputDirectory + "/" + (outputFileName != null ? outputFileName : "decrypted_file.txt");
            FileEncryptor.decryptFile(inputStream, outputFile, password);
        } catch (IOException e) {
            throw new Exception("Error decrypting file: " + e.getMessage(), e);
        }
    }

    private String extractDirectory(String filePath) {
        int lastSeparator = filePath.lastIndexOf(File.separator);
        if (lastSeparator != -1) {
            return filePath.substring(0, lastSeparator);
        }
        return "";
    }
}
