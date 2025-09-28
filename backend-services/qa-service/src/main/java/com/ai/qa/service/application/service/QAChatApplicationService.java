package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.application.dto.QAHistoryDTO;

public interface QAChatApplicationService {

    QAHistoryDTO chat(ChatCompletionCommand command);

    Flux<String> chatStream(ChatCompletionCommand command);
}
