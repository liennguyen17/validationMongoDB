package com.example.demo.request;

import com.example.demo.util.CheckListValueAnnotation;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ValidationRule {
    @CheckListValueAnnotation(listValue = {"error", "warn"})
    private String action;
    @CheckListValueAnnotation(listValue = {"strict", "moderate", "off"})
    private String level;
    private JsonNode jsonSchema;
}
