package com.hanss.ds.service;

import com.hanss.ds.utils.Const;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;

import java.util.*;

@Service
public class DocumentService {
    public static final int TOP_K = 4;
    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveDocument(String content, Map<String, String> metadata) {
        Map<String, String> searchMetadata = Map.of(
                Const.PATH, metadata.get(Const.PATH)
        );

        // Find similar documents
        List<Document> similarDocuments = findSimilarDocuments(content, searchMetadata);
        if (similarDocuments.size() == 1 && Objects.equals(similarDocuments.getFirst().getText(), content)) return;

        if (!similarDocuments.isEmpty()) {
            // Преобразуем список similarDocuments в список idList
            List<String> idList = similarDocuments.stream()
                    .map(Document::getId)
                    .toList();
            // Удаляем дубликаты документов из PGVector
            vectorStore.delete(idList);
        }

        List<Document> documents = List.of(new Document(content, new HashMap<>(metadata)));
        // Add the documents to PGVector
        vectorStore.add(documents);
    }

    public List<Document> findSimilarDocuments(String prompt, Map<String, String> metadata) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(prompt)
                .topK(TOP_K)
                //.similarityThreshold(SIMILARITY_THRESHOLD)
        ;
        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                builder.filterExpression((new FilterExpressionBuilder()).eq(entry.getKey(), entry.getValue()).build());
            }
        }
        return this.vectorStore.similaritySearch(builder.build());
    }
}
