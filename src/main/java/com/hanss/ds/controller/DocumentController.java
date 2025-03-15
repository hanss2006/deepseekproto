package com.hanss.ds.controller;

import com.hanss.ds.dto.VectorDataDTO;
import com.hanss.ds.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/search")
    public List<Document> searchDocuments(@RequestBody VectorDataDTO searchVectorData) {
        return documentService.findSimilarDocuments(searchVectorData.getContent(), searchVectorData.getMetadata());
    }

    @PostMapping("/save")
    public void saveDocument(@RequestBody VectorDataDTO vectorDataDTO) {
        documentService.saveDocument(vectorDataDTO.getContent(), vectorDataDTO.getMetadata());
    }
}