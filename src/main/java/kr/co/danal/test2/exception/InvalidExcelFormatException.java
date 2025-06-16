package kr.co.danal.test2.exception;

public class InvalidExcelFormatException extends RuntimeException {
    public InvalidExcelFormatException(String message) {
        super(message);
    }
}