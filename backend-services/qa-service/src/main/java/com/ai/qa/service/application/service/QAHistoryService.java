package com.ai.qa.service.application.service;

import java.util.List;

import com.ai.qa.service.api.dto.QAHistoryDto;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;

public interface QAHistoryService {

    public QAHistoryDto saveHistory(SaveHistoryCommand command);

    public List<QAHistoryDto> queryUserHistory(QAHistoryQuery query);
}
