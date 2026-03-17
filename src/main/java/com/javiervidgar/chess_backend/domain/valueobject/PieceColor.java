package com.javiervidgar.chess_backend.domain.valueobject;

public enum PieceColor {
  WHITE,
  BLACK;

  public PieceColor opposite() {
    return this == WHITE ? BLACK : WHITE;
  }
}
