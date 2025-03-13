package com.hanss.ds.service;

import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

@Service
public class DocumentService {
    public static final int TOP_K = 5;
    private final OllamaChatModel ollamaChatModel;
    private final VectorStore vectorStore;

    public DocumentService(OllamaChatModel ollamaChatModel, VectorStore vectorStore) {
        this.ollamaChatModel = ollamaChatModel;
        this.vectorStore = vectorStore;
    }

    public void saveDocument(String content, Map<String, Object> metadata) {
        List<Document> documents = List.of(new Document(content, metadata));
        // Add the documents to PGVector
        vectorStore.add(documents);
    }

    public List<Document> findSimilarDocuments(String prompt) {
        return this.vectorStore.similaritySearch(SearchRequest.builder().query(prompt).topK(TOP_K).build());
    }
}
