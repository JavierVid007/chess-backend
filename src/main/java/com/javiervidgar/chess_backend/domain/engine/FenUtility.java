package com.javiervidgar.chess_backend.domain.engine;

import com.javiervidgar.chess_backend.domain.exception.InvalidFenException;
import com.javiervidgar.chess_backend.domain.valueobject.PieceColor;

public class FenUtility {

  public static Board fromFen(String fen) {
    String[] parts = fen.split(" ");
    validateFenLength(parts);

    Board board = new Board();
    parsePiecePlacement(board, parts[0]);
    parseGameMetadata(board, parts);

    return board;
  }

  public static String toFen(Board board) {
    return buildPiecePlacement(board) + " " + buildGameMetadata(board);
  }

  private static void validateFenLength(String[] parts) {
    if (parts.length != 6)
      throw new InvalidFenException("Invalid FEN String: Expected 6 parts");
  }

  private static void parsePiecePlacement(Board board, String piecePlacement) {
    String[] rows = piecePlacement.split("/");
    for (int row = 0; row < 8; row++)
      parseRow(board.getGrid(), row, rows[row]);
  }

  private static void parseRow(char[][] grid, int row, String rowData) {
    int col = 0;
    for (char c : rowData.toCharArray()) {
      if (Character.isDigit(c))
        col += Character.getNumericValue(c);
      else {
        grid[row][col] = c;
        col++;
      }
    }
  }

  private static void parseGameMetadata(Board board, String[] parts) {
    board.setActiveColor(parts[1].equals("w") ? PieceColor.WHITE : PieceColor.BLACK);
    board.setCastlingRights(parts[2]);
    board.setEnPassantTarget(parts[3]);
    board.setHalfMoveClock(Integer.parseInt(parts[4]));
    board.setFullMoveNumber(Integer.parseInt(parts[5]));
  }

  private static String buildPiecePlacement(Board board) {
    StringBuilder placement = new StringBuilder();
    for (int row = 0; row < 8; row++) {
      buildRowPlacement(board.getGrid(), row, placement);
      if (row < 7)
        placement.append("/");
    }
    return placement.toString();
  }

  private static void buildRowPlacement(char[][] grid, int row, StringBuilder placement) {
    int emptyCount = 0;
    for (int col = 0; col < 8; col++) {
      char piece = grid[row][col];
      if (piece == ' ')
        emptyCount++;
      else {
        if (emptyCount > 0) {
          placement.append(emptyCount);
          emptyCount = 0;
        }
        placement.append(piece);
      }
    }
    if (emptyCount > 0)
      placement.append(emptyCount);
  }

  private static String buildGameMetadata(Board board) {
    String activeColorStr = (board.getActiveColor() == PieceColor.WHITE) ? "w" : "b";

    return String.format("%s %s %s %d &d",
        activeColorStr,
        board.getCastlingRights(),
        board.getEnPassantTarget(),
        board.getHalfMoveClock(),
        board.getFullMoveNumber());
  }
}