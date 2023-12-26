package com.example.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class FileEncryptor {

    public static SecretKey generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), "AES");
    }

    public static void encryptFile(InputStream inputStream, String outputFile, String password) throws Exception {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            // Write salt and IV (Initialization Vector) to the beginning of the output file
            fileOutputStream.write(salt);
            fileOutputStream.write(cipher.getIV());

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] encryptedBlock = cipher.update(buffer, 0, bytesRead);
                if (encryptedBlock != null) {
                    fileOutputStream.write(encryptedBlock);
                }
            }

            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                fileOutputStream.write(finalBlock);
            }
        }
    }

    public static void decryptFile(InputStream inputStream, String outputFile, String password) throws Exception {
        byte[] encryptedData = inputStream.readAllBytes();

        // Extract the salt, IV, and encrypted content from the encrypted data
        byte[] salt = Arrays.copyOfRange(encryptedData, 0, 16);
        byte[] iv = Arrays.copyOfRange(encryptedData, 16, 32);
        byte[] encryptedContent = Arrays.copyOfRange(encryptedData, 32, encryptedData.length);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");

        // Use the IV during decryption
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(decryptedContent);
        }
    }
}
