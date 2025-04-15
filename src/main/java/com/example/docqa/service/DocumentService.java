package com.example.docqa.service;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DocumentService {
    Map<String, Object> saveDocument(MultipartFile file, String author, String type) throws TikaException;
    CompletableFuture<Map<String, Object>> saveDocumentAsync(MultipartFile file, String author, String type);
    List<Map<String, String>> searchDocuments(String keyword);
    Map<String, Object> filterDocuments(String author, String type, int page, int size, String sortBy, String sortDir);

}