package com.example.securefileapp.controller;

import com.example.securefileapp.dto.NewsAnalysisRequest;
import com.example.securefileapp.dto.NewsAnalysisResponse;
import com.example.securefileapp.model.NewsAnalysis;
import com.example.securefileapp.model.User;
import com.example.securefileapp.repository.NewsAnalysisRepository;
import com.example.securefileapp.service.FakeNewsDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NewsController {
    
    private final FakeNewsDetectionService fakeNewsDetectionService;
    private final NewsAnalysisRepository newsAnalysisRepository;
    
    @PostMapping("/analyze")
    public ResponseEntity<NewsAnalysisResponse> analyzeNews(
            @Valid @RequestBody NewsAnalysisRequest request,
            Authentication authentication) {
        
        try {
            log.info("Received news analysis request for user: {}", authentication.getName());
            
            // Analyze the news
            NewsAnalysisResponse response = fakeNewsDetectionService.analyzeNews(request);
            
            // Save the analysis to database
            saveAnalysisToDatabase(request, response, authentication);
            
            log.info("News analysis completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error analyzing news: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<NewsAnalysis>> getAnalysisHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        try {
            User user = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page, size);
            
            Page<NewsAnalysis> analyses = newsAnalysisRepository.findByUserOrderByCreatedAtDesc(user, pageable);
            
            return ResponseEntity.ok(analyses);
            
        } catch (Exception e) {
            log.error("Error fetching analysis history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/feedback/{analysisId}")
    public ResponseEntity<Void> provideFeedback(
            @PathVariable Long analysisId,
            @RequestBody Map<String, Object> feedback,
            Authentication authentication) {
        
        try {
            User user = (User) authentication.getPrincipal();
            
            NewsAnalysis analysis = newsAnalysisRepository.findById(analysisId)
                    .orElseThrow(() -> new RuntimeException("Analysis not found"));
            
            // Verify the analysis belongs to the user
            if (!analysis.getUser().getId().equals(user.getId())) {
                return ResponseEntity.forbidden().build();
            }
            
            // Update feedback
            analysis.setUserFeedback((String) feedback.get("feedback"));
            analysis.setFeedbackRating((Integer) feedback.get("rating"));
            
            newsAnalysisRepository.save(analysis);
            
            log.info("Feedback saved for analysis ID: {}", analysisId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error saving feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            Long totalAnalyses = newsAnalysisRepository.countTotalByUser(user);
            Long fakeNewsCount = newsAnalysisRepository.countFakeNewsByUser(user);
            
            double fakeNewsPercentage = totalAnalyses > 0 ? 
                (double) fakeNewsCount / totalAnalyses * 100 : 0;
            
            Map<String, Object> statistics = Map.of(
                "totalAnalyses", totalAnalyses,
                "fakeNewsCount", fakeNewsCount,
                "realNewsCount", totalAnalyses - fakeNewsCount,
                "fakeNewsPercentage", Math.round(fakeNewsPercentage * 100.0) / 100.0
            );
            
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/languages")
    public ResponseEntity<List<Object[]>> getLanguageStatistics() {
        try {
            List<Object[]> languageStats = newsAnalysisRepository.getStatisticsByLanguage();
            return ResponseEntity.ok(languageStats);
            
        } catch (Exception e) {
            log.error("Error fetching language statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private void saveAnalysisToDatabase(NewsAnalysisRequest request, 
                                      NewsAnalysisResponse response, 
                                      Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            NewsAnalysis analysis = new NewsAnalysis();
            analysis.setUser(user);
            analysis.setNewsTitle(request.getTitle());
            analysis.setNewsContent(request.getContent());
            analysis.setDetectedLanguage(response.getDetectedLanguage());
            analysis.setIsFakeNews(response.getIsFakeNews());
            analysis.setConfidenceScore(response.getConfidenceScore());
            analysis.setAnalysisDurationMs(response.getAnalysisDurationMs());
            analysis.setModelUsed(response.getModelUsed());
            analysis.setSourceUrl(request.getSourceUrl());
            analysis.setAuthor(request.getAuthor());
            analysis.setAnalysisFeatures(response.getAnalysisFeatures());
            
            newsAnalysisRepository.save(analysis);
            
            // Update user's analysis count
            user.setAnalysisCount(user.getAnalysisCount() + 1);
            
            log.info("Analysis saved to database with ID: {}", analysis.getId());
            
        } catch (Exception e) {
            log.error("Error saving analysis to database: {}", e.getMessage(), e);
            // Don't throw exception to avoid breaking the main flow
        }
    }
} 