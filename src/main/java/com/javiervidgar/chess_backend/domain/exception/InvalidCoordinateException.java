package com.javiervidgar.chess_backend.domain.exception;

public class InvalidCoordinateException extends ChessDomainException {

  public InvalidCoordinateException(String message) {
    super(message);
  }
}
