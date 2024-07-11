package com.example.demo.exception;

public class CollectionNotFoundException extends RuntimeException{
    public CollectionNotFoundException(String collectionName) {
        super("Collection '" + collectionName + "' does not exist");
    }
}
