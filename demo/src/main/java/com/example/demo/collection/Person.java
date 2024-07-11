package com.example.demo.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import org.springframework.data.annotation.Transient;

@Data
@AllArgsConstructor
public class Person {
    private String firstname;
    private int age;
    private Species species;
    private Address address;
    @Field // Assuming default field type, adjust if needed
    private String theForce;

    @Transient
    private Boolean useTheForce;
    public enum Species {
        HUMAN, WOOKIE, UNKNOWN
    }
}
