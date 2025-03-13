package com.hanss.ds.controller;

import com.hanss.ds.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/search")
    public List<Document> searchDocuments(@RequestParam String prompt) {
        return documentService.findSimilarDocuments(prompt);
    }

    @PostMapping("/save")
    public void saveDocument(@RequestBody String content, @RequestBody Map<String, Object> metadata) {
        documentService.saveDocument(content, metadata);
    }
}