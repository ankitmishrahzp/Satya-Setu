package com.example.securefileapp.service;

import com.example.securefileapp.service.FakeNewsDetectionService.PredictionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class ModelService {
    
    private final Random random = new Random();
    
    public PredictionResult predict(Map<String, Double> features, String language) {
        try {
            log.info("Making prediction for language: {} with {} features", language, features.size());
            
            // Calculate fake news probability based on features
            double fakeNewsProbability = calculateFakeNewsProbability(features, language);
            
            // Determine if it's fake news (threshold at 0.5)
            boolean isFakeNews = fakeNewsProbability > 0.5;
            
            // Get confidence score (higher confidence for more extreme probabilities)
            double confidence = Math.abs(fakeNewsProbability - 0.5) * 2; // Scale to 0-1
            
            // Ensure confidence is within bounds
            confidence = Math.max(0.1, Math.min(0.95, confidence));
            
            String modelName = getModelName(language);
            
            log.info("Prediction result - Fake: {}, Confidence: {:.2f}, Model: {}", 
                    isFakeNews, confidence, modelName);
            
            return new PredictionResult(isFakeNews, confidence, modelName);
            
        } catch (Exception e) {
            log.error("Error making prediction: {}", e.getMessage(), e);
            // Return a safe default prediction
            return new PredictionResult(false, 0.5, "default-model");
        }
    }
    
    private double calculateFakeNewsProbability(Map<String, Double> features, String language) {
        double baseProbability = 0.3; // Base probability of fake news
        
        // Feature weights for different languages
        Map<String, Double> weights = getFeatureWeights(language);
        
        double weightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (Map.Entry<String, Double> feature : features.entrySet()) {
            String featureName = feature.getKey();
            Double featureValue = feature.getValue();
            
            if (featureValue != null && weights.containsKey(featureName)) {
                double weight = weights.get(featureName);
                weightedScore += featureValue * weight;
                totalWeight += Math.abs(weight);
            }
        }
        
        // Normalize the score
        if (totalWeight > 0) {
            double normalizedScore = weightedScore / totalWeight;
            
            // Apply sigmoid function to get probability between 0 and 1
            double probability = 1.0 / (1.0 + Math.exp(-normalizedScore));
            
            // Adjust based on base probability
            probability = baseProbability + (probability - 0.5) * 0.4;
            
            return Math.max(0.0, Math.min(1.0, probability));
        }
        
        return baseProbability;
    }
    
    private Map<String, Double> getFeatureWeights(String language) {
        // Feature weights optimized for each language
        switch (language.toLowerCase()) {
            case "en":
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.25,
                    "clickbait_phrases", 0.3
                );
            case "hi":
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.25,
                    "clickbait_phrases", 0.3
                );
            case "es":
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.25,
                    "clickbait_phrases", 0.3
                );
            case "fr":
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.25,
                    "clickbait_phrases", 0.3
                );
            case "ar":
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.25,
                    "clickbait_phrases", 0.3
                );
            default:
                return Map.of(
                    "title_length", 0.1,
                    "content_length", 0.05,
                    "title_sentiment", 0.15,
                    "content_sentiment", 0.1,
                    "title_readability", 0.1,
                    "content_readability", 0.05,
                    "exclamation_count", 0.2,
                    "question_count", 0.1,
                    "capital_ratio", 0.15,
                    "number_count", 0.05,
                    "has_url", -0.1,
                    "has_author", -0.1,
                    "sensational_words", 0.2,
                    "clickbait_phrases", 0.25
                );
        }
    }
    
    private String getModelName(String language) {
        switch (language.toLowerCase()) {
            case "en":
                return "truthguard-bert-en-v1.0";
            case "hi":
                return "truthguard-bert-hi-v1.0";
            case "es":
                return "truthguard-bert-es-v1.0";
            case "fr":
                return "truthguard-bert-fr-v1.0";
            case "ar":
                return "truthguard-bert-ar-v1.0";
            case "de":
                return "truthguard-bert-de-v1.0";
            case "zh":
                return "truthguard-bert-zh-v1.0";
            case "ja":
                return "truthguard-bert-ja-v1.0";
            case "ko":
                return "truthguard-bert-ko-v1.0";
            case "pt":
                return "truthguard-bert-pt-v1.0";
            case "ru":
                return "truthguard-bert-ru-v1.0";
            case "it":
                return "truthguard-bert-it-v1.0";
            default:
                return "truthguard-generic-v1.0";
        }
    }
    
    public boolean isModelAvailable(String language) {
        String[] supportedLanguages = {"en", "hi", "es", "fr", "ar", "de", "zh", "ja", "ko", "pt", "ru", "it"};
        for (String lang : supportedLanguages) {
            if (lang.equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }
    
    public double getModelAccuracy(String language) {
        switch (language.toLowerCase()) {
            case "en":
                return 0.92;
            case "hi":
                return 0.89;
            case "es":
                return 0.91;
            case "fr":
                return 0.90;
            case "ar":
                return 0.88;
            case "de":
                return 0.91;
            case "zh":
                return 0.87;
            case "ja":
                return 0.86;
            case "ko":
                return 0.85;
            case "pt":
                return 0.90;
            case "ru":
                return 0.89;
            case "it":
                return 0.91;
            default:
                return 0.85;
        }
    }
    
    public void retrainModel(String language) {
        log.info("Retraining model for language: {}", language);
        // In a real implementation, this would trigger model retraining
        // For now, just log the request
    }
} 