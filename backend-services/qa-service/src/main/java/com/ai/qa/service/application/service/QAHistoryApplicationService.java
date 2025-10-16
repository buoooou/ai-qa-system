package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;

import java.util.List;

public interface QAHistoryApplicationService {

    QAHistoryDTO saveHistory(SaveHistoryCommand command);

    List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query);
}
