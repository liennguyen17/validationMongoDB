package com.example.demo.controller;

import com.example.demo.request.SchemaRequest;
import com.example.demo.request.SchemaRequestV2;
import com.example.demo.service.SchemaService;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/validation")
public class SchemaController {
    private final SchemaService schemaService;
    @PostMapping("/schema")
    public ResponseEntity<Void> createCollectionWithSchema(@RequestBody Document schema) {
        schemaService.createCollectionWithSchema(schema);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schema/v1")
    public ResponseEntity<Void> createCollectionWithSchemaV1(@RequestBody SchemaRequest request) {
        schemaService.createCollectionWithSchema1(request.getCollection(), request.getSchema());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schema/v2")
    public ResponseEntity<Void> createCollectionWithSchemaV2(@RequestBody SchemaRequestV2 request) {
        schemaService.createCollectionWithSchema2(request.getCollection(), request.getSchema());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/schema")
    public ResponseEntity<Void> updateCollectionWithSchema(@RequestBody SchemaRequest request){
        schemaService.updateSchema(request.getCollection(), request.getSchema());
        return ResponseEntity.ok().build();
    }



}
