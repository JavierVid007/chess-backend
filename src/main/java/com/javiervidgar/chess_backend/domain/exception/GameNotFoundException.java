package com.javiervidgar.chess_backend.domain.exception;

public class GameNotFoundException extends ChessDomainException {

  public GameNotFoundException(String gameId) {
    super("Game not found with ID: " + gameId);
  }
}
