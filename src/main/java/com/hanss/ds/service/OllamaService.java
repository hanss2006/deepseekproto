package com.hanss.ds.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OllamaService {

    private final OllamaChatModel chatModel;
    private final DocumentService documentService;

    public OllamaService(OllamaChatModel chatModel, DocumentService documentService) {
        this.chatModel = chatModel;
        this.documentService = documentService;
    }

    public String askQuestion(String userQuery) {
        // Ищем релевантные документы в PGVector
        List<Document> documentList = this.documentService.findSimilarDocuments(userQuery, null);

        // Формируем контекст из найденных документов
        String context = documentList.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // Формируем окончательный промпт
        String finalPrompt = "Контекст:\n" + context + "\n\nВопрос: " + userQuery;

        // Отправляем в Ollama для генерации ответа
        return chatModel.call(finalPrompt);
    }
}
