package com.example.docqa.exception;

public class DocumentProcessingException extends RuntimeException {
    public DocumentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
