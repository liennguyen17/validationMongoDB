package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfo {
    private List<IndexField> indexFields;
    private String name;
    private boolean unique;
    private boolean hidden;
    private String indexSize;

}
