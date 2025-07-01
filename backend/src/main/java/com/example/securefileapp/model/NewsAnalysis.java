package com.example.securefileapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "news_title", nullable = false)
    private String newsTitle;
    
    @Column(name = "news_content", columnDefinition = "TEXT", nullable = false)
    private String newsContent;
    
    @Column(name = "detected_language")
    private String detectedLanguage;
    
    @Column(name = "is_fake_news")
    private Boolean isFakeNews;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;
    
    @Column(name = "model_used")
    private String modelUsed;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "user_feedback")
    private String userFeedback;
    
    @Column(name = "feedback_rating")
    private Integer feedbackRating; // 1-5 scale
    
    @ElementCollection
    @CollectionTable(name = "analysis_features", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "feature_value")
    private List<String> analysisFeatures;
    
    @Column(name = "source_url")
    private String sourceUrl;
    
    @Column(name = "author")
    private String author;
    
    @Column(name = "publication_date")
    private LocalDateTime publicationDate;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 