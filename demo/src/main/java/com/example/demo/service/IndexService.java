package com.example.demo.service;

import com.example.demo.exception.CollectionNotFoundException;
import com.example.demo.request.IndexField;
import com.example.demo.request.IndexInfo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@AllArgsConstructor
public class IndexService {
    private final MongoTemplate mongoTemplate;

    public String createIndex(String collectionName, List<Map<String, String>> indexes, Map<String, Object> options) {
        IndexOperations indexOps = mongoTemplate.indexOps(collectionName);

        for (Map<String, String> indexField : indexes) {
            Index index = new Index();
            for (Map.Entry<String, String> entry : indexField.entrySet()) {
                String field = entry.getKey();
                String type = entry.getValue().toUpperCase();
                if ("ASC".equals(type)) {
                    index.on(field, org.springframework.data.domain.Sort.Direction.ASC);
                } else if ("DESC".equals(type)) {
                    index.on(field, org.springframework.data.domain.Sort.Direction.DESC);
                }
            }

            if (options != null) {
                if (Boolean.TRUE.equals(options.get("unique"))) {
                    index.unique();
                }
//                if (Boolean.TRUE.equals(options.get("background"))) {
//                    index.background();
//                }
//                if (Boolean.TRUE.equals(options.get("sparse"))) {
//                    index.sparse();
//                }
                //
            }
            try {
                indexOps.ensureIndex(index);
            } catch (Exception e) {
                log.error("Lỗi khi tạo: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating index: " + e.getMessage(), e);

            }

        }
        return "Index created successfully";
    }

    public String deleteIndex(String collectionName, String indexName) {
        mongoTemplate.indexOps(collectionName).dropIndex(indexName);
        log.info("delete index name: {}", indexName);
        return "Index deleted successfully";
    }

    public void getIndexs(String collectionName) {
        mongoTemplate.indexOps(collectionName).getIndexInfo().forEach(indexInfo -> {
            log.info("index name: {}", indexInfo.getName());
        });
    }

    public List<Document> listIndexes(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        return StreamSupport.stream(collection.listIndexes().spliterator(), false).collect(Collectors.toList());
    }

    public List<IndexInfo> listIndex(String collectionName) {
        if (!collectionExists(collectionName)) {
            throw new CollectionNotFoundException(collectionName);
        }

        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        List<IndexInfo> indexInfoList = new ArrayList<>();

        MongoDatabase database = mongoTemplate.getDb();
        Document collStats = database.runCommand(new Document("collStats", collectionName));

        for (Document indexDoc : collection.listIndexes()) {
            logDocument(indexDoc);

            List<IndexField> indexFields = new ArrayList<>();
            Document keyDoc = (Document) indexDoc.get("key");
            for (String key : keyDoc.keySet()) {
                Object value = keyDoc.get(key);
                indexFields.add(new IndexField(key, value != null ? value.toString() : null));
            }

            String name = indexDoc.getString("name");
            boolean unique = indexDoc.getBoolean("unique", false);
            boolean hidden = indexDoc.getBoolean("hidden", false);
            Long indexSizeInBytes = getIndexSize(collStats, name);
            String indexSize = indexSizeInBytes != null ? formatSizeInKB(indexSizeInBytes) : null;

            IndexInfo indexInfo = new IndexInfo(indexFields, name, unique, hidden, indexSize);

            indexInfoList.add(indexInfo);
        }

        return indexInfoList;
    }

    private boolean collectionExists(String collectionName) {
        MongoDatabase database = mongoTemplate.getDb();
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

//    private void logDocument(Document document) {
//        for (String key : document.keySet()) {
//            Object value = document.get(key);
//            if (value instanceof Document) {
//                // Recursively log nested documents
//                System.out.println("Nested Document - Key: " + key);
//
//                logDocument((Document) value);
//            } else {
//                System.out.println("Key: " + key + ", Value: " + value);
//            }
//        }}

    private void logDocument(Document document) {
        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value instanceof Document) {
                // Recursively log nested documents
                log.info("Nested Document - Key: {}", key);
                logDocument((Document) value);
            } else {
                log.info("Key: {}, Value: {}", key, value);
            }
        }
    }

    private Long getIndexSize(Document collStats, String indexName) {
        Document indexSizes = (Document) collStats.get("indexSizes");
        if (indexSizes != null && indexSizes.containsKey(indexName)) {
            Object size = indexSizes.get(indexName);
            if (size instanceof Integer) {
                return ((Integer) size).longValue();
            } else if (size instanceof Long) {
                return (Long) size;
            }
        }
        return null;
    }

    private String formatSizeInKB(Long sizeInBytes) {
        if (sizeInBytes == null) {
            return null;
        }
        double sizeInKB = sizeInBytes / 1024.0;
        DecimalFormat df = new DecimalFormat("#,##0.0");
        return df.format(sizeInKB) + " KB";
    }


}
