package com.example.demo.request;

import lombok.Data;

@Data
public class updateSchemaRequest {
    private String collectionName;
    private String schema;
}
