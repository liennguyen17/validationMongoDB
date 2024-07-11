package com.example.demo.controller;

import com.example.demo.request.CreateIndexRequest;
import com.example.demo.request.DeleteIndexRequest;
import com.example.demo.request.IndexInfo;
import com.example.demo.request.ListIndexRequest;
import com.example.demo.service.IndexService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class IndexController {
    @Autowired
    private IndexService indexService;

    @PostMapping("/create")
    public String createIndex(@RequestBody CreateIndexRequest request) {
        return indexService.createIndex(request.getCollectionName(), request.getIndexes(), request.getOptions());
    }

    @DeleteMapping("/delete")
    public String deleteIndex(@RequestBody DeleteIndexRequest request){
        return indexService.deleteIndex(request.getCollectionName(), request.getIndexName());
    }

    @GetMapping("/list")
    public List<Document> documentList(@RequestBody ListIndexRequest request){
        return indexService.listIndexes(request.getCollectionName());
    }

    @GetMapping()
    public void getIndex(@RequestParam String collectionName){
        indexService.getIndexs(collectionName);
    }

    @GetMapping("/{collectionName}")
    public List<IndexInfo> listIndexes(@PathVariable String collectionName) {
        return indexService.listIndex(collectionName);
    }
}
