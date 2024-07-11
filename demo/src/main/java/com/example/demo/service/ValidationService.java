package com.example.demo.service;

import com.example.demo.request.ValidationRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ValidationService {
    @Autowired
    private MongoTemplate mongoTemplate;
    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();

    public String createCollectionWithValidation(String collectionName, Document jsonSchema) {
        MongoDatabase database = mongoTemplate.getDb();
        ValidationOptions validationOptions = new ValidationOptions().validator(jsonSchema);
        database.createCollection(collectionName, new CreateCollectionOptions().validationOptions(validationOptions));
        return "Collection created with validation successfully";
    }



    public void updateSchema(String collectionName, String schema) {
        Document command = new Document("collMod", collectionName)
                .append("validator", Document.parse(schema));

        mongoTemplate.getDb().runCommand(command);
        log.info("Update schema of collection: {}",collectionName);
//        return "update schema";
    }

    public void addValidationRuleToDocument(String collectionName, String documentId, ValidationRule validationRule){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(documentId));
        Document schemaDocument = Document.parse(validationRule.getJsonSchema().toString());

        Document validationOptions = new Document();
        validationOptions.put("validator", new Document("$jsonSchema", schemaDocument));
        validationOptions.put("validationLevel", validationRule.getLevel());
        validationOptions.put("validationAction", validationRule.getAction());

        Document update = new Document("$set", new Document("validationRule", validationOptions));
        mongoTemplate.getCollection(collectionName).updateOne(query.getQueryObject(), update);
    }

    public void addValidationRuleToCollection(String collectionName, ValidationRule validationRule){
        validateJsonSchema(validationRule);
        if(!collectionExists(collectionName)){
            createCollection(collectionName);
        }else {
            validateExistingDocuments(collectionName, validationRule);
        }

        addValidationDocument(collectionName, validationRule);
    }

//    public void addValidationRule(String collectionName, ValidationRule validationRule){
//        validateValidationRule(validationRule);
//        addValidationDocument(collectionName, validationRule);
//
//    }

    public void addValidationDocument(String collectionName, ValidationRule validationRule) {
        Document schemaDoc = Document.parse(validationRule.getJsonSchema().toString());

        Document validationOptions = new Document();
        validationOptions.put("validator", new Document("$jsonSchema", schemaDoc));
        validationOptions.put("validationLevel", validationRule.getLevel());
        validationOptions.put("validationAction", validationRule.getAction());

        mongoTemplate.getDb().runCommand(new Document("collMod", collectionName)
                .append("validator", validationOptions.get("validator"))
                .append("validationLevel", validationOptions.get("validationLevel"))
                .append("validationAction", validationOptions.get("validationAction")));
    }

    private void validateJsonSchema(ValidationRule validationRule) throws IllegalArgumentException {
        // Validate JSON schema
        try {
            JsonSchema schema = schemaFactory.getJsonSchema(validationRule.getJsonSchema());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON schema provided.", e);
        }
    }

    private void validateExistingDocuments(String collectionName, ValidationRule validationRule) throws IllegalArgumentException {
        try {
            JsonSchema schema = schemaFactory.getJsonSchema(validationRule.getJsonSchema());
            for (JsonNode document : mongoTemplate.findAll(JsonNode.class, collectionName)) {
                ProcessingReport report = schema.validate(document);
                if (!report.isSuccess()) {
                    throw new IllegalArgumentException("Existing documents do not meet the new validation rules: " + report);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error validating existing documents.", e);
        }
    }

    public boolean collectionExists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }

    public void createCollection(String collectionName) {
        mongoTemplate.createCollection(collectionName);
    }

    public List<JsonNode> getDocuments(String collectionName) {
        return mongoTemplate.findAll(JsonNode.class, collectionName);
    }



}
