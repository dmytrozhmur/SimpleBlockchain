package org.example.exceptions;

public class EncryptionException extends RuntimeException {
    public EncryptionException(String message) {
        super(message);
    }
}
