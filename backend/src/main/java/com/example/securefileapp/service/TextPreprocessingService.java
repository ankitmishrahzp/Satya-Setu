package com.example.securefileapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class TextPreprocessingService {
    
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    
    public String preprocessText(String text, String language) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        try {
            String processed = text;
            
            // Step 1: Remove HTML tags
            processed = removeHtmlTags(processed);
            
            // Step 2: Remove URLs
            processed = removeUrls(processed);
            
            // Step 3: Remove emails
            processed = removeEmails(processed);
            
            // Step 4: Language-specific preprocessing
            processed = applyLanguageSpecificPreprocessing(processed, language);
            
            // Step 5: Normalize whitespace
            processed = normalizeWhitespace(processed);
            
            // Step 6: Remove excessive punctuation
            processed = removeExcessivePunctuation(processed);
            
            // Step 7: Trim and final cleanup
            processed = processed.trim();
            
            log.debug("Text preprocessing completed. Original length: {}, Processed length: {}", 
                     text.length(), processed.length());
            
            return processed;
            
        } catch (Exception e) {
            log.error("Error preprocessing text: {}", e.getMessage(), e);
            return text; // Return original text if preprocessing fails
        }
    }
    
    private String removeHtmlTags(String text) {
        return HTML_TAG_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String removeUrls(String text) {
        return URL_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String removeEmails(String text) {
        return EMAIL_PATTERN.matcher(text).replaceAll(" ");
    }
    
    private String applyLanguageSpecificPreprocessing(String text, String language) {
        switch (language.toLowerCase()) {
            case "en":
                return preprocessEnglish(text);
            case "hi":
                return preprocessHindi(text);
            case "es":
                return preprocessSpanish(text);
            case "fr":
                return preprocessFrench(text);
            case "ar":
                return preprocessArabic(text);
            case "zh":
                return preprocessChinese(text);
            case "ja":
                return preprocessJapanese(text);
            case "ko":
                return preprocessKorean(text);
            default:
                return preprocessGeneric(text);
        }
    }
    
    private String preprocessEnglish(String text) {
        // English-specific preprocessing
        String processed = text;
        
        // Remove common English contractions for analysis
        processed = processed.replaceAll("\\b(can't|cannot)\\b", "can not");
        processed = processed.replaceAll("\\b(won't|will not)\\b", "will not");
        processed = processed.replaceAll("\\b(don't|do not)\\b", "do not");
        processed = processed.replaceAll("\\b(doesn't|does not)\\b", "does not");
        processed = processed.replaceAll("\\b(haven't|have not)\\b", "have not");
        processed = processed.replaceAll("\\b(hasn't|has not)\\b", "has not");
        processed = processed.replaceAll("\\b(hadn't|had not)\\b", "had not");
        processed = processed.replaceAll("\\b(isn't|is not)\\b", "is not");
        processed = processed.replaceAll("\\b(aren't|are not)\\b", "are not");
        processed = processed.replaceAll("\\b(wasn't|was not)\\b", "was not");
        processed = processed.replaceAll("\\b(weren't|were not)\\b", "were not");
        
        return processed;
    }
    
    private String preprocessHindi(String text) {
        // Hindi-specific preprocessing
        String processed = text;
        
        // Remove common Hindi abbreviations
        processed = processed.replaceAll("\\b(डॉ\\.|डॉक्टर)\\b", "डॉक्टर");
        processed = processed.replaceAll("\\b(श्री\\.|श्रीमान)\\b", "श्रीमान");
        processed = processed.replaceAll("\\b(श्रीमती\\.|श्रीमती)\\b", "श्रीमती");
        
        return processed;
    }
    
    private String preprocessSpanish(String text) {
        // Spanish-specific preprocessing
        String processed = text;
        
        // Remove common Spanish abbreviations
        processed = processed.replaceAll("\\b(Sr\\.|Señor)\\b", "Señor");
        processed = processed.replaceAll("\\b(Sra\\.|Señora)\\b", "Señora");
        processed = processed.replaceAll("\\b(Dr\\.|Doctor)\\b", "Doctor");
        
        return processed;
    }
    
    private String preprocessFrench(String text) {
        // French-specific preprocessing
        String processed = text;
        
        // Remove common French abbreviations
        processed = processed.replaceAll("\\b(M\\.|Monsieur)\\b", "Monsieur");
        processed = processed.replaceAll("\\b(Mme\\.|Madame)\\b", "Madame");
        processed = processed.replaceAll("\\b(Dr\\.|Docteur)\\b", "Docteur");
        
        return processed;
    }
    
    private String preprocessArabic(String text) {
        // Arabic-specific preprocessing
        String processed = text;
        
        // Normalize Arabic text
        processed = processed.replaceAll("\\b(د\\.|دكتور)\\b", "دكتور");
        processed = processed.replaceAll("\\b(أ\\.|أستاذ)\\b", "أستاذ");
        
        return processed;
    }
    
    private String preprocessChinese(String text) {
        // Chinese-specific preprocessing
        String processed = text;
        
        // Remove common Chinese abbreviations
        processed = processed.replaceAll("\\b(博士|博士\\.)\\b", "博士");
        processed = processed.replaceAll("\\b(教授|教授\\.)\\b", "教授");
        
        return processed;
    }
    
    private String preprocessJapanese(String text) {
        // Japanese-specific preprocessing
        String processed = text;
        
        // Remove common Japanese abbreviations
        processed = processed.replaceAll("\\b(博士|博士\\.)\\b", "博士");
        processed = processed.replaceAll("\\b(教授|教授\\.)\\b", "教授");
        
        return processed;
    }
    
    private String preprocessKorean(String text) {
        // Korean-specific preprocessing
        String processed = text;
        
        // Remove common Korean abbreviations
        processed = processed.replaceAll("\\b(박사|박사\\.)\\b", "박사");
        processed = processed.replaceAll("\\b(교수|교수\\.)\\b", "교수");
        
        return processed;
    }
    
    private String preprocessGeneric(String text) {
        // Generic preprocessing for unsupported languages
        return text;
    }
    
    private String normalizeWhitespace(String text) {
        return MULTIPLE_SPACES.matcher(text).replaceAll(" ");
    }
    
    private String removeExcessivePunctuation(String text) {
        // Remove excessive punctuation marks (keep one)
        text = text.replaceAll("!{2,}", "!");
        text = text.replaceAll("\\?{2,}", "?");
        text = text.replaceAll("\\.{2,}", ".");
        text = text.replaceAll(",{2,}", ",");
        text = text.replaceAll(";{2,}", ";");
        text = text.replaceAll(":{2,}", ":");
        
        return text;
    }
    
    public String extractKeywords(String text, String language) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Simple keyword extraction based on frequency
        String[] words = text.toLowerCase().split("\\s+");
        java.util.Map<String, Integer> wordCount = new java.util.HashMap<>();
        
        for (String word : words) {
            if (word.length() > 3 && !isStopWord(word, language)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        
        // Return top 5 keywords
        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(java.util.Map.Entry::getKey)
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);
    }
    
    private boolean isStopWord(String word, String language) {
        String[] stopWords = getStopWords(language);
        for (String stopWord : stopWords) {
            if (stopWord.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
    
    private String[] getStopWords(String language) {
        switch (language.toLowerCase()) {
            case "en":
                return new String[]{"the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by"};
            case "hi":
                return new String[]{"का", "की", "के", "है", "हैं", "और", "या", "लेकिन", "में", "पर", "से", "को", "के लिए"};
            case "es":
                return new String[]{"el", "la", "los", "las", "un", "una", "y", "o", "pero", "en", "de", "a", "por", "con"};
            case "fr":
                return new String[]{"le", "la", "les", "un", "une", "et", "ou", "mais", "dans", "de", "à", "pour", "avec"};
            case "ar":
                return new String[]{"ال", "في", "من", "إلى", "على", "عن", "مع", "هذا", "هذه", "التي", "الذي"};
            default:
                return new String[]{};
        }
    }
} 