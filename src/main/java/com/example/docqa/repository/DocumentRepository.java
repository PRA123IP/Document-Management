package com.example.docqa.repository;
import com.example.docqa.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByContentContainingIgnoreCase(String keyword);

    Page<Document> findByAuthorContainingIgnoreCaseAndDocTypeContainingIgnoreCase(String author, String type, Pageable pageable);
}
