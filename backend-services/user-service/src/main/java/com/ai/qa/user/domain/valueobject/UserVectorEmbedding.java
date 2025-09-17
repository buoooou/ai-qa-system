package com.ai.qa.user.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVectorEmbedding {
    private float[] embedding;
    private String model;
    private int dimension;
    
    public UserVectorEmbedding(float[] embedding) {
        this.embedding = embedding;
        this.dimension = embedding.length;
        this.model = "default";
    }
    
    public double cosineSimilarity(UserVectorEmbedding other) {
        if (this.embedding.length != other.embedding.length) {
            throw new IllegalArgumentException("Embedding dimensions must match");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < this.embedding.length; i++) {
            dotProduct += this.embedding[i] * other.embedding[i];
            norm1 += this.embedding[i] * this.embedding[i];
            norm2 += other.embedding[i] * other.embedding[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    @Override
    public String toString() {
        return "UserVectorEmbedding{" +
                "dimension=" + dimension +
                ", model='" + model + '\'' +
                '}';
    }
}