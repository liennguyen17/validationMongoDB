package com.example.demo.request;

import lombok.Data;
import org.bson.Document;

@Data
public class SchemaRequestV2 {
    private String collection;
    private Document schema;
}
