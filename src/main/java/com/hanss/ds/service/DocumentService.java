package com.hanss.ds.service;

import com.hanss.ds.utils.Const;
import com.hanss.ds.utils.UrlEncoder;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class DocumentService {
    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveDocument(String content, Map<String, String> metadata) throws NoSuchAlgorithmException {
        String contentHash = UrlEncoder.hashContent(content);
        Map<String, String> searchMetadata = Map.of(
                Const.FILENAME, metadata.get(Const.FILENAME)
        );

        // Find similar documents
        List<Document> similarDocuments = findSimilarDocuments(content, searchMetadata);
        if (!similarDocuments.isEmpty() && Objects.equals(similarDocuments.getFirst().getMetadata().get(Const.CONTENT_HASH), contentHash)) return;

        if (!similarDocuments.isEmpty()) {
            // Преобразуем список similarDocuments в список idList
            List<String> idList = similarDocuments.stream()
                    .map(Document::getId)
                    .toList();
            // Удаляем дубликаты документов из PGVector
            vectorStore.delete(idList);
        }


        // Создаем ByteArrayResource из строки
        ByteArrayResource resource = new ByteArrayResource(content.getBytes());

        // Создаем конфигурацию MarkdownDocumentReader (по желанию)
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        // Создаем объект MarkdownDocumentReader
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        // tag as external knowledge in the vector store's metadata
        List<Document> splitDocuments = tokenTextSplitter.split(reader.get());
        int i=0;
        for (Document splitDocument: splitDocuments) {
            splitDocument.getMetadata().putAll(metadata);
            splitDocument.getMetadata().put(Const.CHUNK, String.valueOf(i++));
            splitDocument.getMetadata().put(Const.CONTENT_HASH, contentHash);
        }

        // Sending batch of documents to vector store
        // Load
        vectorStore.write(splitDocuments);
    }

    public List<Document> findSimilarDocuments(String prompt, Map<String, String> metadata) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(prompt)
                .topK(Const.TOP_K)
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
