package com.example.demo.service;

import com.example.demo.collection.Person;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.FileInfo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SchemaService {
    private MongoJsonSchema mongoJsonSchema;
    private MongoTemplate mongoTemplate;
    private final MongoOperations mongoOperations;
    private final MongoMappingContext mongoMappingContext;
    @Autowired
    public SchemaService(MongoTemplate mongoTemplate, MongoOperations mongoOperations, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
        this.mongoMappingContext = mongoMappingContext;
    }


    public void createCollectionWithSchema(Document schema) {
        // Drop collection if it exists
        if (mongoTemplate.collectionExists(Person.class)) {
            mongoTemplate.getCollection(Person.class.getSimpleName()).drop();
        }

        // Command to create collection with validation schema
        Document command = new Document();
        command.append("create", "person");
        command.append("validator", new Document("$jsonSchema", schema));

        mongoTemplate.getDb().runCommand(command);
    }


    public void createCollectionWithSchema1(String collectionName, String schemaJson) {
        // Drop collection if it exists
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }

        // Command to create collection with validation schema
        Document command = new Document();
        command.append("create", collectionName);
        command.append("validator", Document.parse(String.format("{ $jsonSchema: %s }", schemaJson)));

        // Execute command to create collection with schema
        mongoTemplate.getDb().runCommand(command);
    }

    public void createCollectionWithSchema2(String collectionName, Document schema) {
        // Drop collection if it exists
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }

        // Command to create collection with validation schema
        Document command = new Document();

        command.append("create", collectionName);
        command.append("validator", new Document("$jsonSchema", schema));

//        ValidationOptions validationOptions = new ValidationOptions();
//        validationOptions.va

        // Execute command to create collection with schema
        mongoTemplate.getDb().runCommand(command);
    }

    public void createValidation(String collectionName, String schema) {
        // Drop collection if it exists
//        if (mongoTemplate.collectionExists(collectionName)) {
//            mongoTemplate.dropCollection(collectionName);
//        }

        // Command to create collection with validation schema

        Document doc = new Document();
        ValidationOptions validationOptions = new ValidationOptions();
        validationOptions.validator(doc);
        validationOptions.validationAction(ValidationAction.ERROR);
        validationOptions.validationLevel(ValidationLevel.STRICT);
        CreateCollectionOptions cco = new CreateCollectionOptions();
        cco.validationOptions(validationOptions);


//        mongoTemplate.createCollection(FileInfo.class, CollectionOptions.empty()
//                .validator(Validator.schema(MongoJsonSchema.builder().required()
//                        .build())))



//        command.append("create", collectionName);
//        command.append("validator", new Document("$jsonSchema", schema));
//
////        ValidationOptions validationOptions = new ValidationOptions();
////        validationOptions.va
//
//        // Execute command to create collection with schema
//        mongoTemplate.getDb().runCommand(command);
    }


    public void updateSchema(String collectionName, String schema) {
        //check if collection exists
        if (!mongoTemplate.getCollectionNames().contains(collectionName)) {
            log.info("collectionName: {} not exist -> create collection first", collectionName);
            mongoTemplate.createCollection(collectionName);
            log.info("collectionName: {}, create collection success", collectionName);
        }
        log.info("Start executeCommand update collection: {}, schema: {}", collectionName, schema);
        mongoTemplate.executeCommand(new Document(Map.of(
                "collMod", collectionName,
                "validator", Document.parse(schema)
        )));

        log.info("End executeCommand update schema");
    }



}
