package com.example.docqa.service;


import com.example.docqa.model.Document;
import com.example.docqa.repository.DocumentRepository;
import com.example.docqa.service.impl.DocumentServiceImpl;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceImplTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Captor
    private ArgumentCaptor<Document> documentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDocument_successfulUpload() throws Exception {
        // Given
        String sampleContent = "This is a test PDF content for Tika.";
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", sampleContent.getBytes());

        // When
        Map<String, Object> result = documentService.saveDocument(file, "Pradip", "PDF");

        // Then
        assertEquals("Document uploaded successfully", result.get("message"));
        verify(documentRepository, times(1)).save(documentCaptor.capture());

        Document savedDoc = documentCaptor.getValue();
        assertEquals("Pradip", savedDoc.getAuthor());
        assertEquals("PDF", savedDoc.getDocType());
        assertTrue(savedDoc.getContent().contains("This is a test"));
        assertNotNull(savedDoc.getUploadDate());
    }

    @Test
    void testSaveDocument_throwsIOException() throws Exception {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Fake IO"));

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                documentService.saveDocument(file, "Author", "TXT"));

        assertTrue(exception.getMessage().contains("Error reading document content"));
    }

    @Test
    void testSearchDocuments_returnsResults() {
        // Given
        Document doc = new Document();
        doc.setAuthor("Pradip");
        doc.setDocType("PDF");
        doc.setContent("This is a test document content for keyword match.");
        when(documentRepository.findByContentContainingIgnoreCase("test"))
                .thenReturn(List.of(doc));

        // When
        List<Map<String, String>> result = documentService.searchDocuments("test");

        // Then
        assertEquals(1, result.size());
        assertEquals("Pradip", result.get(0).get("author"));
        assertEquals("PDF", result.get(0).get("type"));
        assertTrue(result.get(0).get("snippet").contains("test document"));
    }

    @Test
    void testSearchDocuments_emptyResults() {
        when(documentRepository.findByContentContainingIgnoreCase("no-match"))
                .thenReturn(Collections.emptyList());

        List<Map<String, String>> result = documentService.searchDocuments("no-match");
        assertTrue(result.isEmpty());
    }
    @Test
    void testSaveDocumentAsync() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", "Hello Async".getBytes());

        // When
        CompletableFuture<Map<String, Object>> future = documentService.saveDocumentAsync(file, "Bob", "text");

        // Then
        assertTrue(future.isDone());
        assertEquals("Document uploaded successfully", future.get().get("message"));
    }

    @Test
    void testFilterDocuments() {
        // Given
        Document doc = new Document();
        doc.setId(1L);
        doc.setAuthor("Sam");
        doc.setDocType("pdf");
        doc.setContent("Filtered document content");
        doc.setUploadDate(new Date());

        List<Document> docs = List.of(doc);
        Page<Document> page = new PageImpl<>(docs);
        when(documentRepository.findByAuthorContainingIgnoreCaseAndDocTypeContainingIgnoreCase(
                eq("Sam"), eq("pdf"), any(Pageable.class))).thenReturn(page);

        // When
        Map<String, Object> result = documentService.filterDocuments("Sam", "pdf", 0, 10, "uploadDate", "desc");

        // Then
        assertEquals(1, ((List<?>) result.get("documents")).size());
        assertEquals(0, result.get("currentPage"));
        assertEquals(1, result.get("totalPages"));
    }
}
