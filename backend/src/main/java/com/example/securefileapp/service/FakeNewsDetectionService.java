package com.example.securefileapp.service;

import com.example.securefileapp.dto.NewsAnalysisRequest;
import com.example.securefileapp.dto.NewsAnalysisResponse;
import com.example.securefileapp.model.NewsAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FakeNewsDetectionService {
    
    private final LanguageDetectionService languageDetectionService;
    private final TextPreprocessingService textPreprocessingService;
    private final ModelService modelService;
    
    public FakeNewsDetectionService(
            LanguageDetectionService languageDetectionService,
            TextPreprocessingService textPreprocessingService,
            ModelService modelService) {
        this.languageDetectionService = languageDetectionService;
        this.textPreprocessingService = textPreprocessingService;
        this.modelService = modelService;
    }
    
    public NewsAnalysisResponse analyzeNews(NewsAnalysisRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Language Detection
            String detectedLanguage = request.getLanguage();
            if (detectedLanguage == null || detectedLanguage.isEmpty()) {
                detectedLanguage = languageDetectionService.detectLanguage(
                    request.getTitle() + " " + request.getContent()
                );
            }
            
            // Step 2: Text Preprocessing
            String preprocessedTitle = textPreprocessingService.preprocessText(request.getTitle(), detectedLanguage);
            String preprocessedContent = textPreprocessingService.preprocessText(request.getContent(), detectedLanguage);
            
            // Step 3: Feature Extraction
            Map<String, Double> features = extractFeatures(preprocessedTitle, preprocessedContent, detectedLanguage);
            
            // Step 4: Model Prediction
            PredictionResult prediction = modelService.predict(features, detectedLanguage);
            
            // Step 5: Generate Explanation
            String explanation = generateExplanation(features, prediction, detectedLanguage);
            String recommendation = generateRecommendation(prediction.getConfidence(), detectedLanguage);
            
            long analysisDuration = System.currentTimeMillis() - startTime;
            
            return NewsAnalysisResponse.builder()
                .newsTitle(request.getTitle())
                .newsContent(request.getContent())
                .detectedLanguage(detectedLanguage)
                .isFakeNews(prediction.isFakeNews())
                .confidenceScore(prediction.getConfidence())
                .analysisDurationMs(analysisDuration)
                .modelUsed(prediction.getModelName())
                .createdAt(new Date())
                .sourceUrl(request.getSourceUrl())
                .author(request.getAuthor())
                .featureScores(features)
                .analysisFeatures(extractFeatureNames(features))
                .explanation(explanation)
                .recommendation(recommendation)
                .build();
                
        } catch (Exception e) {
            log.error("Error analyzing news: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze news content", e);
        }
    }
    
    private Map<String, Double> extractFeatures(String title, String content, String language) {
        Map<String, Double> features = new HashMap<>();
        
        // Text-based features
        features.put("title_length", (double) title.length());
        features.put("content_length", (double) content.length());
        features.put("title_word_count", (double) title.split("\\s+").length);
        features.put("content_word_count", (double) content.split("\\s+").length);
        
        // Sentiment features
        features.put("title_sentiment", calculateSentiment(title, language));
        features.put("content_sentiment", calculateSentiment(content, language));
        
        // Readability features
        features.put("title_readability", calculateReadability(title, language));
        features.put("content_readability", calculateReadability(content, language));
        
        // Linguistic features
        features.put("exclamation_count", countExclamations(title + " " + content));
        features.put("question_count", countQuestions(title + " " + content));
        features.put("capital_ratio", calculateCapitalRatio(title + " " + content));
        features.put("number_count", countNumbers(title + " " + content));
        
        // URL and source features
        features.put("has_url", hasUrl(title + " " + content) ? 1.0 : 0.0);
        features.put("has_author", hasAuthor(title + " " + content) ? 1.0 : 0.0);
        
        // Language-specific features
        addLanguageSpecificFeatures(features, title, content, language);
        
        return features;
    }
    
    private double calculateSentiment(String text, String language) {
        // Simplified sentiment calculation
        // In a real implementation, you would use language-specific sentiment analysis
        String[] positiveWords = getPositiveWords(language);
        String[] negativeWords = getNegativeWords(language);
        
        String[] words = text.toLowerCase().split("\\s+");
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : words) {
            if (Arrays.asList(positiveWords).contains(word)) {
                positiveCount++;
            }
            if (Arrays.asList(negativeWords).contains(word)) {
                negativeCount++;
            }
        }
        
        if (positiveCount + negativeCount == 0) {
            return 0.0;
        }
        
        return (double) (positiveCount - negativeCount) / (positiveCount + negativeCount);
    }
    
    private double calculateReadability(String text, String language) {
        // Simplified readability score (Flesch Reading Ease equivalent)
        String[] sentences = text.split("[.!?]+");
        String[] words = text.split("\\s+");
        String[] syllables = text.toLowerCase().split("[aeiou]+");
        
        if (sentences.length == 0 || words.length == 0) {
            return 0.0;
        }
        
        double avgSentenceLength = (double) words.length / sentences.length;
        double avgSyllablesPerWord = (double) syllables.length / words.length;
        
        return 206.835 - (1.015 * avgSentenceLength) - (84.6 * avgSyllablesPerWord);
    }
    
    private int countExclamations(String text) {
        return (int) text.chars().filter(ch -> ch == '!').count();
    }
    
    private int countQuestions(String text) {
        return (int) text.chars().filter(ch -> ch == '?').count();
    }
    
    private double calculateCapitalRatio(String text) {
        if (text.isEmpty()) return 0.0;
        long capitalCount = text.chars().filter(Character::isUpperCase).count();
        return (double) capitalCount / text.length();
    }
    
    private int countNumbers(String text) {
        return (int) text.chars().filter(Character::isDigit).count();
    }
    
    private boolean hasUrl(String text) {
        return text.contains("http://") || text.contains("https://") || text.contains("www.");
    }
    
    private boolean hasAuthor(String text) {
        String[] authorIndicators = {"by", "author", "written by", "reported by"};
        String lowerText = text.toLowerCase();
        return Arrays.stream(authorIndicators).anyMatch(lowerText::contains);
    }
    
    private void addLanguageSpecificFeatures(Map<String, Double> features, String title, String content, String language) {
        // Language-specific feature extraction
        switch (language.toLowerCase()) {
            case "en":
                addEnglishFeatures(features, title, content);
                break;
            case "hi":
                addHindiFeatures(features, title, content);
                break;
            case "es":
                addSpanishFeatures(features, title, content);
                break;
            case "fr":
                addFrenchFeatures(features, title, content);
                break;
            case "ar":
                addArabicFeatures(features, title, content);
                break;
            default:
                addGenericFeatures(features, title, content);
        }
    }
    
    private void addEnglishFeatures(Map<String, Double> features, String title, String content) {
        String[] sensationalWords = {"shocking", "amazing", "incredible", "unbelievable", "breaking"};
        String[] clickbaitWords = {"you won't believe", "this will shock you", "what happens next"};
        
        String combinedText = (title + " " + content).toLowerCase();
        
        features.put("sensational_words", countWords(combinedText, sensationalWords));
        features.put("clickbait_phrases", countPhrases(combinedText, clickbaitWords));
    }
    
    private void addHindiFeatures(Map<String, Double> features, String title, String content) {
        String[] sensationalWords = {"आश्चर्यजनक", "अविश्वसनीय", "चौंकाने वाला", "बड़ी खबर"};
        String[] clickbaitWords = {"आप विश्वास नहीं करेंगे", "यह आपको चौंका देगा"};
        
        String combinedText = title + " " + content;
        
        features.put("sensational_words", countWords(combinedText, sensationalWords));
        features.put("clickbait_phrases", countPhrases(combinedText, clickbaitWords));
    }
    
    private void addSpanishFeatures(Map<String, Double> features, String title, String content) {
        String[] sensationalWords = {"sorprendente", "increíble", "asombroso", "impactante"};
        String[] clickbaitWords = {"no vas a creer", "esto te sorprenderá"};
        
        String combinedText = (title + " " + content).toLowerCase();
        
        features.put("sensational_words", countWords(combinedText, sensationalWords));
        features.put("clickbait_phrases", countPhrases(combinedText, clickbaitWords));
    }
    
    private void addFrenchFeatures(Map<String, Double> features, String title, String content) {
        String[] sensationalWords = {"surprenant", "incroyable", "étonnant", "choquant"};
        String[] clickbaitWords = {"vous ne croirez pas", "cela va vous surprendre"};
        
        String combinedText = (title + " " + content).toLowerCase();
        
        features.put("sensational_words", countWords(combinedText, sensationalWords));
        features.put("clickbait_phrases", countPhrases(combinedText, clickbaitWords));
    }
    
    private void addArabicFeatures(Map<String, Double> features, String title, String content) {
        String[] sensationalWords = {"مذهل", "لا يصدق", "صادم", "مفاجئ"};
        String[] clickbaitWords = {"لن تصدق", "سيصدمك"};
        
        String combinedText = title + " " + content;
        
        features.put("sensational_words", countWords(combinedText, sensationalWords));
        features.put("clickbait_phrases", countPhrases(combinedText, clickbaitWords));
    }
    
    private void addGenericFeatures(Map<String, Double> features, String title, String content) {
        // Generic features for unsupported languages
        features.put("generic_sensational_score", 0.0);
        features.put("generic_clickbait_score", 0.0);
    }
    
    private double countWords(String text, String[] words) {
        return Arrays.stream(words)
            .mapToDouble(word -> text.contains(word) ? 1.0 : 0.0)
            .sum();
    }
    
    private double countPhrases(String text, String[] phrases) {
        return Arrays.stream(phrases)
            .mapToDouble(phrase -> text.contains(phrase) ? 1.0 : 0.0)
            .sum();
    }
    
    private String[] getPositiveWords(String language) {
        switch (language.toLowerCase()) {
            case "en":
                return new String[]{"good", "great", "excellent", "amazing", "wonderful", "positive", "happy"};
            case "hi":
                return new String[]{"अच्छा", "बढ़िया", "शानदार", "उत्कृष्ट", "सकारात्मक"};
            case "es":
                return new String[]{"bueno", "excelente", "maravilloso", "positivo", "feliz"};
            case "fr":
                return new String[]{"bon", "excellent", "merveilleux", "positif", "heureux"};
            case "ar":
                return new String[]{"جيد", "ممتاز", "رائع", "إيجابي", "سعيد"};
            default:
                return new String[]{"good", "great", "excellent"};
        }
    }
    
    private String[] getNegativeWords(String language) {
        switch (language.toLowerCase()) {
            case "en":
                return new String[]{"bad", "terrible", "awful", "horrible", "negative", "sad"};
            case "hi":
                return new String[]{"बुरा", "भयानक", "खराब", "नकारात्मक", "दुखी"};
            case "es":
                return new String[]{"malo", "terrible", "horrible", "negativo", "triste"};
            case "fr":
                return new String[]{"mauvais", "terrible", "horrible", "négatif", "triste"};
            case "ar":
                return new String[]{"سيء", "رهيب", "فظيع", "سلبي", "حزين"};
            default:
                return new String[]{"bad", "terrible", "awful"};
        }
    }
    
    private List<String> extractFeatureNames(Map<String, Double> features) {
        return new ArrayList<>(features.keySet());
    }
    
    private String generateExplanation(Map<String, Double> features, PredictionResult prediction, String language) {
        StringBuilder explanation = new StringBuilder();
        
        if (prediction.isFakeNews()) {
            explanation.append(getLocalizedString("likely_fake", language));
        } else {
            explanation.append(getLocalizedString("likely_real", language));
        }
        
        explanation.append(" (");
        explanation.append(String.format("%.1f%%", prediction.getConfidence() * 100));
        explanation.append(" ").append(getLocalizedString("confidence", language)).append(")");
        
        // Add key factors
        List<String> keyFactors = identifyKeyFactors(features);
        if (!keyFactors.isEmpty()) {
            explanation.append("\n\n").append(getLocalizedString("key_factors", language)).append(":\n");
            for (String factor : keyFactors) {
                explanation.append("• ").append(factor).append("\n");
            }
        }
        
        return explanation.toString();
    }
    
    private String generateRecommendation(double confidence, String language) {
        if (confidence > 0.8) {
            return getLocalizedString("high_confidence_recommendation", language);
        } else if (confidence > 0.6) {
            return getLocalizedString("medium_confidence_recommendation", language);
        } else {
            return getLocalizedString("low_confidence_recommendation", language);
        }
    }
    
    private List<String> identifyKeyFactors(Map<String, Double> features) {
        List<String> factors = new ArrayList<>();
        
        if (features.get("sensational_words") > 0) {
            factors.add("Contains sensational language");
        }
        if (features.get("clickbait_phrases") > 0) {
            factors.add("Uses clickbait phrases");
        }
        if (features.get("exclamation_count") > 3) {
            factors.add("Excessive use of exclamation marks");
        }
        if (features.get("capital_ratio") > 0.3) {
            factors.add("High use of capital letters");
        }
        
        return factors;
    }
    
    private String getLocalizedString(String key, String language) {
        // Simplified localization - in a real app, you'd use a proper i18n system
        Map<String, Map<String, String>> translations = new HashMap<>();
        
        // English translations
        Map<String, String> en = new HashMap<>();
        en.put("likely_fake", "This news appears to be fake");
        en.put("likely_real", "This news appears to be real");
        en.put("confidence", "confidence");
        en.put("key_factors", "Key factors");
        en.put("high_confidence_recommendation", "High confidence in this analysis");
        en.put("medium_confidence_recommendation", "Medium confidence - verify with additional sources");
        en.put("low_confidence_recommendation", "Low confidence - manual verification recommended");
        translations.put("en", en);
        
        // Hindi translations
        Map<String, String> hi = new HashMap<>();
        hi.put("likely_fake", "यह समाचार फर्जी प्रतीत होता है");
        hi.put("likely_real", "यह समाचार वास्तविक प्रतीत होता है");
        hi.put("confidence", "विश्वास");
        hi.put("key_factors", "मुख्य कारक");
        hi.put("high_confidence_recommendation", "इस विश्लेषण में उच्च विश्वास");
        hi.put("medium_confidence_recommendation", "मध्यम विश्वास - अतिरिक्त स्रोतों से सत्यापित करें");
        hi.put("low_confidence_recommendation", "कम विश्वास - मैनुअल सत्यापन की सिफारिश");
        translations.put("hi", hi);
        
        Map<String, String> langMap = translations.getOrDefault(language, en);
        return langMap.getOrDefault(key, en.get(key));
    }
    
    // Inner classes for prediction results
    public static class PredictionResult {
        private final boolean isFakeNews;
        private final double confidence;
        private final String modelName;
        
        public PredictionResult(boolean isFakeNews, double confidence, String modelName) {
            this.isFakeNews = isFakeNews;
            this.confidence = confidence;
            this.modelName = modelName;
        }
        
        public boolean isFakeNews() { return isFakeNews; }
        public double getConfidence() { return confidence; }
        public String getModelName() { return modelName; }
    }
} 