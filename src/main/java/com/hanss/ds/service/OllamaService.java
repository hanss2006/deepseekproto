package com.hanss.ds.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    private final OllamaChatModel chatModel;

    public OllamaService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chatWithModel(String userInput) {
        return chatModel.call(new UserMessage(userInput));
    }
}
