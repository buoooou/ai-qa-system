import {
  customProvider,
  extractReasoningMiddleware,
  wrapLanguageModel,
} from 'ai';
// import { gateway } from '@ai-sdk/gateway';
import { deepseek } from '@ai-sdk/deepseek';
import { isTestEnvironment } from '../constants';

export const myProvider = isTestEnvironment
  ? (() => {
      const {
        artifactModel,
        chatModel,
        reasoningModel,
        titleModel,
      } = require('./models.mock');
      return customProvider({
        languageModels: {
          'chat-model': chatModel,
          'chat-model-reasoning': reasoningModel,
          'title-model': titleModel,
          'artifact-model': artifactModel,
        },
      });
    })()
  : customProvider({
      languageModels: {
        'chat-model': deepseek.languageModel('deepseek-chat'),
        'chat-model-reasoning': wrapLanguageModel({
          model: deepseek.languageModel('deepseek-chat'),
          middleware: extractReasoningMiddleware({ tagName: 'think' }),
        }),
        'title-model': deepseek.languageModel('deepseek-chat'),
        'artifact-model': deepseek.languageModel('deepseek-chat'),
      },
    });
