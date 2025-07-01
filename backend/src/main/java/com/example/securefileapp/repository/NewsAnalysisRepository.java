package com.example.securefileapp.repository;

import com.example.securefileapp.model.NewsAnalysis;
import com.example.securefileapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsAnalysisRepository extends JpaRepository<NewsAnalysis, Long> {
    
    // Find all analyses by user
    Page<NewsAnalysis> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Find analyses by user and fake news status
    List<NewsAnalysis> findByUserAndIsFakeNewsOrderByCreatedAtDesc(User user, Boolean isFakeNews);
    
    // Find analyses by language
    List<NewsAnalysis> findByDetectedLanguageOrderByCreatedAtDesc(String language);
    
    // Find analyses by date range
    @Query("SELECT na FROM NewsAnalysis na WHERE na.createdAt BETWEEN :startDate AND :endDate")
    List<NewsAnalysis> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    // Find analyses by user and date range
    @Query("SELECT na FROM NewsAnalysis na WHERE na.user = :user AND na.createdAt BETWEEN :startDate AND :endDate")
    List<NewsAnalysis> findByUserAndDateRange(@Param("user") User user,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    // Count fake news analyses by user
    @Query("SELECT COUNT(na) FROM NewsAnalysis na WHERE na.user = :user AND na.isFakeNews = true")
    Long countFakeNewsByUser(@Param("user") User user);
    
    // Count total analyses by user
    @Query("SELECT COUNT(na) FROM NewsAnalysis na WHERE na.user = :user")
    Long countTotalByUser(@Param("user") User user);
    
    // Find analyses with high confidence
    @Query("SELECT na FROM NewsAnalysis na WHERE na.confidenceScore >= :minConfidence")
    List<NewsAnalysis> findByHighConfidence(@Param("minConfidence") Double minConfidence);
    
    // Find analyses by model used
    List<NewsAnalysis> findByModelUsedOrderByCreatedAtDesc(String modelUsed);
    
    // Get statistics by language
    @Query("SELECT na.detectedLanguage, COUNT(na), AVG(na.confidenceScore) " +
           "FROM NewsAnalysis na GROUP BY na.detectedLanguage")
    List<Object[]> getStatisticsByLanguage();
    
    // Get fake news percentage by language
    @Query("SELECT na.detectedLanguage, " +
           "COUNT(CASE WHEN na.isFakeNews = true THEN 1 END) * 100.0 / COUNT(na) " +
           "FROM NewsAnalysis na GROUP BY na.detectedLanguage")
    List<Object[]> getFakeNewsPercentageByLanguage();
} 