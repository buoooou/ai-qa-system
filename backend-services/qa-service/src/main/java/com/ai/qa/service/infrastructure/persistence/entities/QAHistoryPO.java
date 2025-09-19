package com.ai.qa.service.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;
@Data                    // Lombok注解：自动生成getter、setter、toString、equals、hashCode方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor
@Entity
@Table(name= "qa_history_cdw")
public class QAHistoryPO {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "create_time")
    private LocalDateTime createTime;


}
