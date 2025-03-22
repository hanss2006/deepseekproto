package com.hanss.ds.service;

import com.hanss.ds.utils.Const;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OllamaService {

    private final OllamaChatModel chatModel;
    private final DocumentService documentService;
    private final ChatClient ragChatClient;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;


    public OllamaService(OllamaChatModel chatModel, DocumentService documentService, ChatClient ragChatClient) {
        this.chatModel = chatModel;
        this.documentService = documentService;
        this.ragChatClient = ragChatClient;
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(Const.SIMILARITY_THRESHOLD)
                        .vectorStore(documentService.getVectorStore())
                        .build())
                .build();
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

    public String queryLLM(String question) {
        return ragChatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
    }
}
