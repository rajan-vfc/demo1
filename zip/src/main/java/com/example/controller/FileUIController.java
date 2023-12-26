package com.example.controller;

import com.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
@RequestMapping("/")
public class FileUIController {

    private final FileService fileService;

    @Autowired
    public FileUIController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public String showFileForm() {
        return "index";
    }

    @GetMapping("/encrypt")
    public String showEncryptForm() {
        return "encrypt";
    }

    @GetMapping("/decrypt")
    public String showDecryptForm() {
        return "decrypt";
    }

    @PostMapping("/performOperation")
    public String handleFileOperation(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam(value = "outputFileName", required = false) String outputFileName,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String outputDirectory = extractDirectory(file.getOriginalFilename());
            String outputFile = outputDirectory + "/" + (outputFileName != null ? outputFileName : "encrypted_file.enc");
            fileService.encryptAndSave(file, password, outputFileName);
            redirectAttributes.addFlashAttribute("encryptedFile", outputFile);
            redirectAttributes.addFlashAttribute("successMessage", "File encrypted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing file: " + e.getMessage());
        }

        return "redirect:/encrypt";
    }

    private String extractDirectory(String filePath) {
        int lastSeparator = filePath.lastIndexOf(File.separator);
        if (lastSeparator != -1) {
            return filePath.substring(0, lastSeparator);
        }
        return "";
    }
}
