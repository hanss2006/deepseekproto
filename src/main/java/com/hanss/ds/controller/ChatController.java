package com.hanss.ds.controller;

import com.hanss.ds.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OllamaService ollamaService;

    public ChatController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/query")
    public String chat(@RequestBody String userInput) {
        return ollamaService.askQuestion(userInput);
    }

    @PostMapping("/rag")
    public ResponseEntity<String> query(@RequestParam(value = "question") String question) {
        try {
            String response = ollamaService.queryLLM(question);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("[RAG_CHAT][QUERY][EXCEPTION: {}]", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
