package com.example.demo.request;

import lombok.Data;

@Data
public class CreateIndex_1Request {
    private String collectionName;
    private String indexName;
    private String indexType;
}
