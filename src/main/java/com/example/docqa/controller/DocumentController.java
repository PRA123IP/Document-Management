package com.example.docqa.controller;


import com.example.docqa.service.DocumentService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
                                            @RequestParam("author") String author,
                                            @RequestParam("type") String type) throws TikaException {
        return ResponseEntity.ok(documentService.saveDocument(file, author, type));
    }

    @PostMapping("/upload-async")
    public CompletableFuture<ResponseEntity<?>> uploadDocumentAsync(@RequestParam("file") MultipartFile file,
                                                                    @RequestParam("author") String author,
                                                                    @RequestParam("type") String type) throws TikaException {
        return documentService.saveDocumentAsync(file, author, type)
                .thenApply(ResponseEntity::ok);
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(@RequestParam("query") String query) {
        return ResponseEntity.ok(documentService.searchDocuments(query));
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filterDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                documentService.filterDocuments(author, type, page, size, sortBy, sortDir)
        );
    }
}

