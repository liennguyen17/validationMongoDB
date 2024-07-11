package com.example.demo.request;

import com.mongodb.client.model.IndexOptions;
import lombok.Data;

@Data
public class CreateIndexOptionRequest {
    private String collectionName;
    private String indexName;
    private IndexOptions indexType;
}
