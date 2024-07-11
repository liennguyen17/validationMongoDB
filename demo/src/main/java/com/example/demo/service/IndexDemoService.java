package com.example.demo.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class IndexDemoService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String createIndex1(String collectionName, Document index, IndexOptions options) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        collection.createIndex(index, options); //sd raw command
        return "Index created successfully";
    }


    //tạo và mặc định kiểu chỉ mục ASC tăng đần
    public String create(String collectionName, String indexName, String indexType){
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(indexName, Sort.Direction.ASC).unique());
        return "Index created successfully";
    }
    //createIndex truyen kiểu chỉ mục
    public String createIndex(String collectionName, String indexName, String indexType) {
        Sort.Direction direction;
        // Kiểm tra giá trị của indexType và xác định direction dựa trên nó
        if ("ASC".equalsIgnoreCase(indexType)) {
            direction = Sort.Direction.ASC;
        } else if ("DESC".equalsIgnoreCase(indexType)) {
            direction = Sort.Direction.DESC;
        } else {
            // Nếu indexType không hợp lệ, mặc định sử dụng ASC
            direction = Sort.Direction.ASC;
        }
        // Tạo chỉ mục với direction đã xác định
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(indexName, direction).unique());
        return "Index created successfully";
    }

    //lấy danh sách index
    public List<Document> listIndexes(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        return StreamSupport.stream(collection.listIndexes().spliterator(), false)
                .collect(Collectors.toList());
    }

    public String deleteIndex(String collectionName, String indexName) {
        mongoTemplate.indexOps(collectionName).dropIndex(indexName);
        log.info("delete index name: {}", indexName);
        return "Index deleted successfully";
    }


    //get all index
    public void getIndexs(String collectionName){
        mongoTemplate.indexOps(collectionName).getIndexInfo().forEach(indexInfo -> {
            log.info("index name: {}", indexInfo.getName());
        });
    }




}
