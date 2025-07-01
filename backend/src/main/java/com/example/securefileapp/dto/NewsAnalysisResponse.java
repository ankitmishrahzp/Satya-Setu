package com.example.securefileapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsAnalysisResponse {
    private Long id;
    private String newsTitle;
    private String newsContent;
    private String detectedLanguage;
    private Boolean isFakeNews;
    private Double confidenceScore;
    private Long analysisDurationMs;
    private String modelUsed;
    private LocalDateTime createdAt;
    private String sourceUrl;
    private String author;
    private Map<String, Double> featureScores;
    private List<String> analysisFeatures;
    private String explanation;
    private String recommendation;
} 