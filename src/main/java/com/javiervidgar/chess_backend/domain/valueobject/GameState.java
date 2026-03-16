package com.javiervidgar.chess_backend.domain.valueobject;

public enum GameState {
  WAITING_FOR_OPPONENT,
  ONGOING,
  CHECKMATE,
  STALEMATE,
  DRAW_AGREEMENT,
  RESIGNATION,
  TIMEOUT;
}
