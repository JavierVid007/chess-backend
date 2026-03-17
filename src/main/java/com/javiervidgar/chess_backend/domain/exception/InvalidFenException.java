package com.javiervidgar.chess_backend.domain.exception;

public class InvalidFenException extends ChessDomainException {

  public InvalidFenException(String message) {
    super(message);
  }
}
