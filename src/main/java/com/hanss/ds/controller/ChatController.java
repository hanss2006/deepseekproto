package com.hanss.ds.controller;

import com.hanss.ds.service.OllamaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OllamaService ollamaService;

    public ChatController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public String chat(@RequestBody String userInput) {
        return ollamaService.askQuestion(userInput);
    }
}
