package com.javiervidgar.chess_backend.domain.valueobject;

public record Coordinate(int row, int col) {

  public Coordinate {
    if (row < 0 || row > 7 || col < 0 || col > 7) {
      throw new IllegalArgumentException("Coordenada fuera del tablero: fila " + row + ", columna " + col);
    }
  }

  public static Coordinate fromAlgebraic(String square) {
    if (square == null || square.length() < 2) {
      throw new IllegalArgumentException("Casilla inválida: " + square);
    }

    int col = square.charAt(0) - 'a';
    int row = 8 - Character.getNumericValue(square.charAt(1));

    return new Coordinate(row, col);
  }

  public String toAlgebraic() {
    char file = (char) ('a' + col);
    int rank = 8 - row;
    return "" + file + rank;
  }

}
