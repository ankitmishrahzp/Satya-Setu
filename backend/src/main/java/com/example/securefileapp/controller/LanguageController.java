package com.example.securefileapp.controller;

import com.example.securefileapp.service.LanguageDetectionService;
import com.example.securefileapp.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LanguageController {
    
    private final LanguageDetectionService languageDetectionService;
    private final ModelService modelService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSupportedLanguages() {
        try {
            String[] supportedLanguages = {"en", "hi", "es", "fr", "ar", "de", "zh", "ja", "ko", "pt", "ru", "it"};
            
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> languages = new HashMap<>();
            
            for (String langCode : supportedLanguages) {
                Map<String, Object> langInfo = new HashMap<>();
                langInfo.put("name", languageDetectionService.getLanguageName(langCode));
                langInfo.put("code", langCode);
                langInfo.put("modelAvailable", modelService.isModelAvailable(langCode));
                langInfo.put("accuracy", modelService.getModelAccuracy(langCode));
                
                languages.put(langCode, langInfo);
            }
            
            response.put("languages", languages);
            response.put("totalSupported", supportedLanguages.length);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching supported languages: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/detect")
    public ResponseEntity<Map<String, Object>> detectLanguage(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
            }
            
            String detectedLanguage = languageDetectionService.detectLanguage(text);
            String languageName = languageDetectionService.getLanguageName(detectedLanguage);
            boolean isSupported = languageDetectionService.isLanguageSupported(detectedLanguage);
            boolean modelAvailable = modelService.isModelAvailable(detectedLanguage);
            double accuracy = modelService.getModelAccuracy(detectedLanguage);
            
            Map<String, Object> response = Map.of(
                "detectedLanguage", detectedLanguage,
                "languageName", languageName,
                "isSupported", isSupported,
                "modelAvailable", modelAvailable,
                "accuracy", accuracy
            );
            
            log.info("Language detected: {} ({}) for text length: {}", 
                    languageName, detectedLanguage, text.length());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error detecting language: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{languageCode}")
    public ResponseEntity<Map<String, Object>> getLanguageInfo(@PathVariable String languageCode) {
        try {
            if (!languageDetectionService.isLanguageSupported(languageCode)) {
                return ResponseEntity.notFound().build();
            }
            
            String languageName = languageDetectionService.getLanguageName(languageCode);
            boolean modelAvailable = modelService.isModelAvailable(languageCode);
            double accuracy = modelService.getModelAccuracy(languageCode);
            
            Map<String, Object> response = Map.of(
                "code", languageCode,
                "name", languageName,
                "modelAvailable", modelAvailable,
                "accuracy", accuracy,
                "modelName", modelService.getModelName(languageCode)
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching language info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 