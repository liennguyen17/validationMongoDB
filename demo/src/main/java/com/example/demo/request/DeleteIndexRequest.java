package com.example.demo.request;

import lombok.Data;

@Data
public class DeleteIndexRequest {
    private String indexName;
    private String collectionName;
}
