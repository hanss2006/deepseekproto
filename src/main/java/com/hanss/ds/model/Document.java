package com.hanss.ds.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank
    @NotEmpty
    @NotNull
    @Column(name = "content", columnDefinition = "text")
    private String content;

    @NotNull
    @Column(name = "vector", columnDefinition = "vector")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] vector;

    @Column(name = "content_hash", unique = true)
    private String contentHash;

    @Column(name = "path", unique = true)
    private String path;

}