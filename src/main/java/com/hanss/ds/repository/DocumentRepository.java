package com.hanss.ds.repository;

import com.hanss.ds.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    boolean existsByContentHash(String contentHash);
}