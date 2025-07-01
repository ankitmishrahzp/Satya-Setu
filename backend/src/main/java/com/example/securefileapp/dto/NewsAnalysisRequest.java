package com.example.securefileapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsAnalysisRequest {
    @NotBlank(message = "News title is required")
    @Size(max = 500, message = "News title must be less than 500 characters")
    private String title;
    
    @NotBlank(message = "News content is required")
    @Size(max = 10000, message = "News content must be less than 10000 characters")
    private String content;
    
    private String sourceUrl;
    private String author;
    private String language; // Optional, will auto-detect if not provided
} 