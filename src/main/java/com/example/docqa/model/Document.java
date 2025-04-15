package com.example.docqa.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String docType;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Date uploadDate;
}
