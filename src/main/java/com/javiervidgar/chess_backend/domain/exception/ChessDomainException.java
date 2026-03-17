package com.javiervidgar.chess_backend.domain.exception;

public abstract class ChessDomainException extends RuntimeException {

  public ChessDomainException(String message) {
    super(message);
  }
}
