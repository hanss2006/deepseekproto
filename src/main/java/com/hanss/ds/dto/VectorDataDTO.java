package com.hanss.ds.dto;

import lombok.Data;

import java.util.Map;

@Data
public class VectorDataDTO {
    private String content;
    private Map<String, String> metadata;
}
