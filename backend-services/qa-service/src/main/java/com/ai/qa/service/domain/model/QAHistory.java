package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QAHistory {

    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createTime;

    private QARAG rag;

    // 公共构造函数，供基础设施层使用
    public QAHistory(Long id) {
        this.id = id;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * 获取带RAG上下文的答案
     * @param question 问题
     * @return 增强后的答案
     */
    public String getAnswer(String question) {
        if (rag != null) {
            String context = rag.getContext();
            return answer + " " + context;
        }
        return answer;
    }

    /**
     * 获取RAG增强答案
     */
    public String getRAGAnswer() {
        if (rag != null) {
            return getAnswer(question);
        }
        return answer;
    }

    /**
     * 创建新的问答历史记录
     * @param userId 用户ID
     * @param question 问题
     * @param answer 答案
     * @return 新的问答历史记录
     */
    public static QAHistory createNew(Long userId, String question, String answer) {
        QAHistory history = new QAHistory(null); // ID将由数据库自动生成
        history.userId = userId;
        history.question = question;
        history.answer = answer;
        return history;
    }

    /**
     * 设置RAG上下文
     * @param rag RAG对象
     */
    public void setRAG(QARAG rag) {
        this.rag = rag;
    }

    /**
     * 获取RAG对象
     * @return RAG对象
     */
    public QARAG getRAG() {
        return this.rag;
    }
}
