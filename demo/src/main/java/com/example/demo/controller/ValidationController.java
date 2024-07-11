package com.example.demo.controller;

import com.example.demo.request.ValidationRule;
import com.example.demo.request.updateSchemaRequest;
import com.example.demo.service.ValidationService;
import jakarta.validation.Valid;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {
    @Autowired
    private ValidationService validationService;

    @PostMapping("/create")
    public String createCollectionWithValidation(@RequestParam String collectionName, @RequestBody Document jsonSchema) {
        return validationService.createCollectionWithValidation(collectionName, jsonSchema);
    }

    @PostMapping("/update")
    public String updateCollectionWithValidation(@RequestBody updateSchemaRequest request) {
        validationService.updateSchema(request.getCollectionName(), request.getSchema());
        return "update schema";
    }

    @PostMapping("/{collectionName}/{documentId}")
    public ResponseEntity<Void> addValidation(@PathVariable String collectionName, @PathVariable String documentId, @RequestBody ValidationRule request){
        validationService.addValidationRuleToDocument(collectionName, documentId, request);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{collectionName}")
//    public ResponseEntity<Void> addValidationDocument(@PathVariable String collectionName, @RequestBody ValidationRule request){
//        validationService.addValidationDocument(collectionName, request);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/{collectionName}")
    public ResponseEntity<Void> addValidationRuleDocument(@PathVariable String collectionName, @Valid @RequestBody ValidationRule request) {
        try {
            validationService.addValidationRuleToCollection(collectionName, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
