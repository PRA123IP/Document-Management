package com.example.docqa.service.impl;
import com.example.docqa.model.Document;
import com.example.docqa.repository.DocumentRepository;
import com.example.docqa.service.DocumentService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    private final Tika tika = new Tika();

    @Override
    public Map<String, Object> saveDocument(MultipartFile file, String author, String type)  {
        try {
            String content = tika.parseToString(file.getInputStream());
            Document document = new Document();
            document.setAuthor(author);
            document.setDocType(type);
            document.setContent(content);
            document.setUploadDate(new Date());
            documentRepository.save(document);
            return Map.of("message", "Document uploaded successfully");
        } catch (IOException e) {
            throw new RuntimeException("Error reading document content", e);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, String>> searchDocuments(String keyword) {
        List<Document> results = documentRepository.findByContentContainingIgnoreCase(keyword);
        return results.stream().map(doc -> Map.of(
                "author", doc.getAuthor(),
                "type", doc.getDocType(),
                "snippet", doc.getContent().substring(0, Math.min(200, doc.getContent().length()))
        )).collect(Collectors.toList());
    }
    @Async
    @Override
    public CompletableFuture<Map<String, Object>> saveDocumentAsync(MultipartFile file, String author, String type) {
        return CompletableFuture.completedFuture(saveDocument(file, author, type));
    }
    @Override
    public Map<String, Object> filterDocuments(String author, String type, int page, int size, String sortBy, String sortDir) {
        author = (author != null) ? author : "";
        type = (type != null) ? type : "";

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documentPage = documentRepository.findByAuthorContainingIgnoreCaseAndDocTypeContainingIgnoreCase(author, type, pageable);

        List<Map<String, Object>> content = documentPage.getContent().stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("author", doc.getAuthor());
                    map.put("type", doc.getDocType());
                    map.put("uploadDate", doc.getUploadDate());
                    map.put("snippet", doc.getContent().substring(0, Math.min(200, doc.getContent().length())));
                    return map;
                }).collect(Collectors.toList());

        return Map.of(
                "documents", content,
                "currentPage", documentPage.getNumber(),
                "totalPages", documentPage.getTotalPages(),
                "totalElements", documentPage.getTotalElements()
        );
    }
}