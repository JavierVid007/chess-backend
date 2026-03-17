package com.javiervidgar.chess_backend.domain.exception;

public class IllegalMoveException extends ChessDomainException {

  public IllegalMoveException(String message) {
    super(message);
  }
}
