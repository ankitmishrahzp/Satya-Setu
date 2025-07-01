package com.example.securefileapp.service;

import com.optimaize.languagedetector.LanguageDetector;
import com.optimaize.languagedetector.LanguageDetectorBuilder;
import com.optimaize.languagedetector.i18n.LdLocale;
import com.optimaize.languagedetector.ngram.NgramExtractors;
import com.optimaize.languagedetector.profiles.LanguageProfile;
import com.optimaize.languagedetector.profiles.LanguageProfileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class LanguageDetectionService {
    
    private final LanguageDetector languageDetector;
    
    public LanguageDetectionService() {
        try {
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
            this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();
        } catch (IOException e) {
            log.error("Failed to initialize language detector", e);
            throw new RuntimeException("Language detector initialization failed", e);
        }
    }
    
    public String detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "en"; // Default to English
        }
        
        try {
            // Clean the text for better detection
            String cleanedText = cleanTextForDetection(text);
            
            if (cleanedText.length() < 10) {
                return "en"; // Too short for reliable detection
            }
            
            com.optimaize.languagedetector.result.DetectedLanguage detected = 
                languageDetector.detect(cleanedText);
            
            if (detected.isReliable()) {
                String languageCode = detected.getLocale().getLanguage();
                log.info("Detected language: {} with confidence: {}", languageCode, detected.getProbability());
                return languageCode;
            } else {
                log.warn("Language detection not reliable, defaulting to English");
                return "en";
            }
        } catch (Exception e) {
            log.error("Error detecting language: {}", e.getMessage(), e);
            return "en"; // Default to English on error
        }
    }
    
    private String cleanTextForDetection(String text) {
        return text.replaceAll("[0-9]", "") // Remove numbers
                  .replaceAll("[^\\p{L}\\s]", "") // Keep only letters and spaces
                  .replaceAll("\\s+", " ") // Normalize whitespace
                  .trim();
    }
    
    public boolean isLanguageSupported(String languageCode) {
        String[] supportedLanguages = {"en", "hi", "es", "fr", "ar", "de", "zh", "ja", "ko", "pt", "ru", "it"};
        for (String lang : supportedLanguages) {
            if (lang.equalsIgnoreCase(languageCode)) {
                return true;
            }
        }
        return false;
    }
    
    public String getLanguageName(String languageCode) {
        switch (languageCode.toLowerCase()) {
            case "en": return "English";
            case "hi": return "Hindi";
            case "es": return "Spanish";
            case "fr": return "French";
            case "ar": return "Arabic";
            case "de": return "German";
            case "zh": return "Chinese";
            case "ja": return "Japanese";
            case "ko": return "Korean";
            case "pt": return "Portuguese";
            case "ru": return "Russian";
            case "it": return "Italian";
            default: return "Unknown";
        }
    }
} 