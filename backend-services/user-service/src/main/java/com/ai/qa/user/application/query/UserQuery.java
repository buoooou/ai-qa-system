package com.ai.qa.user.application.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery {
    
    private Long userId;
    
    private String username;
    
    private Integer limit;
    
    private Double similarityThreshold;
}