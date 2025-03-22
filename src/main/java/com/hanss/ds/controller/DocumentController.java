package com.hanss.ds.controller;

import com.hanss.ds.dto.VectorDataDTO;
import com.hanss.ds.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    public void saveDocument(@RequestBody VectorDataDTO vectorDataDTO)  {
        try {
            documentService.saveDocument(vectorDataDTO.getContent(), vectorDataDTO.getMetadata());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving document", e);
        }

    }
}
