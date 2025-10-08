package com.ai.qa.service.domain.model;

import lombok.Getter;

/**
 * QARAG 领域模型，用于存储 RAG 相关信息
 */
@Getter
public class QARAG {

    private final String context; // 检索到的上下文
    private final String generatedAnswer; // 基于上下文生成的答案

    public QARAG(String context, String generatedAnswer) {
        this.context = context;
        this.generatedAnswer = generatedAnswer;
    }
}
