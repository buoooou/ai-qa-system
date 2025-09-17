package com.ai.qa.service.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QARAG {
    
    private String context;
    private String source;
    private double relevanceScore;
    
    public QARAG() {
    }
    
    public QARAG(String context, String source) {
        this.context = context;
        this.source = source;
        this.relevanceScore = 1.0; // 默认相关性分数
    }
    
    /**
     * 获取上下文信息
     * @return 上下文字符串
     */
    public String getContext() {
        return this.context;
    }
    
    /**
     * 设置上下文信息
     * @param context 上下文
     */
    public void setContext(String context) {
        this.context = context;
    }
    
    /**
     * 创建RAG上下文
     * @param context 上下文
     * @param source 来源
     * @return RAG对象
     */
    public static QARAG createContext(String context, String source) {
        return new QARAG(context, source);
    }
}
