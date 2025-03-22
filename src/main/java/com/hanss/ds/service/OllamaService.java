package com.hanss.ds.service;

import com.hanss.ds.utils.Const;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.DEFAULT_CHAT_MEMORY_CONVERSATION_ID;

@Service
public class OllamaService {

    private final ChatClient ragChatClient;
    private final DocumentService documentService;

    public OllamaService(DocumentService documentService, ChatClient ragChatClient) {
        this.ragChatClient = ragChatClient;
        this.documentService = documentService;
    }

    public String queryLLM(String question) {
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(Const.SIMILARITY_THRESHOLD)
                        .vectorStore(documentService.getVectorStore())
                        .build())
                .build();
        return ragChatClient.prompt()
                .advisors(
                        retrievalAugmentationAdvisor,
                        // Chat memory helps us keep context when using the chatbot for up to 10 previous messages.
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory(), DEFAULT_CHAT_MEMORY_CONVERSATION_ID, 10),
                        new SimpleLoggerAdvisor()
                        )
                .user(question)
                .call()
                .content();
    }
}
