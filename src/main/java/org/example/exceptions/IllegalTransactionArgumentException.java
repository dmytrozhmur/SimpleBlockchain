package org.example.exceptions;

public class IllegalTransactionArgumentException extends IllegalArgumentException {
    public IllegalTransactionArgumentException(String s) {
        super(s);
    }
}
