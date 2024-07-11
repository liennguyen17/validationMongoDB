package com.example.demo.request;

import com.mongodb.client.model.IndexOptions;
import lombok.Data;
import org.bson.Document;

@Data
public class IndexRequest {
    private Document index;
    private Document options;
}
