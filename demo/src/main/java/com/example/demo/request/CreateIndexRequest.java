package com.example.demo.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateIndexRequest {
    private String collectionName;
    private List<Map<String, String>> indexes;
    private Map<String, Object> options;
}
