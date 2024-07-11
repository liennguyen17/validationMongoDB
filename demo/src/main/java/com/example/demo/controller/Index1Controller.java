package com.example.demo.controller;

import com.example.demo.request.CreateIndex_1Request;
import com.example.demo.service.IndexDemoService;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/indexes")
public class Index1Controller {
    @Autowired
    private IndexDemoService indexService;

    @PostMapping("/createRow")
    public String createIndex(@RequestParam String collectionName, @RequestBody Map<String, Object> requestBody) {
        Document index = new Document((Map<String, Object>) requestBody.get("index"));
        IndexOptions options = null;
        if (requestBody.containsKey("options")) {
            options = new IndexOptions();
            Map<String, Object> optionsMap = (Map<String, Object>) requestBody.get("options");
            if (optionsMap.containsKey("unique")) {
                options.unique((Boolean) optionsMap.get("unique"));
            }

            // Thêm các tùy chọn khác nếu cần
        }
        return indexService.createIndex1(collectionName, index, options);
    }

//    @PostMapping("/createRow")
//    public String createIndexRow(@RequestParam String collectionName, @RequestBody IndexRequest request) {
//        return indexService.createIndex1(collectionName, request.getIndex(), request.getOptions());
//    }

    @GetMapping("/list")
    public List<Document> listIndexes(@RequestParam String collectionName) {
        return indexService.listIndexes(collectionName);
    }

    @DeleteMapping("/delete")
    public String deleteIndex(@RequestParam String collectionName, @RequestParam String indexName) {
        return indexService.deleteIndex(collectionName, indexName);
    }

    @GetMapping()
    public void getIndex(@RequestParam String collectionName){
        indexService.getIndexs(collectionName);
    }

    @PostMapping("/create")
    public String createIndex(@RequestBody CreateIndex_1Request request){
        return indexService.create(request.getCollectionName(), request.getIndexName(), request.getIndexType());
    }

    @PostMapping("/create/type")
    public String create(@RequestBody CreateIndex_1Request request){
        return indexService.createIndex(request.getCollectionName(), request.getIndexName(), request.getIndexType());
    }
}

