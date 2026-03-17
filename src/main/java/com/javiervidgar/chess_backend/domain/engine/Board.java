package com.javiervidgar.chess_backend.domain.engine;

import com.javiervidgar.chess_backend.domain.valueobject.Coordinate;
import com.javiervidgar.chess_backend.domain.valueobject.PieceColor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {

  private char[][] grid;

  private PieceColor activeColor;
  private String castlingRights;
  private String enPassantTarget;
  private int halfMoveClock;
  private int fullMoveNumber;

  public Board() {
    this.grid = new char[8][8];
    for (int i = 0; i < 8; i++)
      for (int j = 0; j < 8; j++)
        grid[i][j] = ' ';
  }

  public void makeMove(String moveNotation) {
    String cleanMove = moveNotation.replace("-", "");
    Coordinate start = Coordinate.fromAlgebraic(cleanMove.substring(0, 2));
    Coordinate end = Coordinate.fromAlgebraic(cleanMove.substring(2, 4));

    char piece = grid[start.row()][start.col()];
    char targetPiece = grid[end.row()][end.col()];

    boolean isPawn = Character.toLowerCase(piece) == 'p';
    boolean isEnPassant = isPawn && start.col() != end.col() && targetPiece == ' ';
    boolean isCapture = targetPiece != ' ' || isEnPassant;

    executeEnPassantCaptureIfNeeded(start, end, isEnPassant);
    executeCastlingIfNeeded(start, end, piece);

    movePieceOnGrid(start, end, piece);

    executePromotionIfNeeded(end, cleanMove);

    updateHalfMoveClock(isPawn, isCapture);
    updateEnPassantTarget(start, end, isPawn);
    updateCastlingRights(start, end, piece);
    advanceTurn();
  }

  private void executeEnPassantCaptureIfNeeded(Coordinate start, Coordinate end, boolean isEnPassant) {
    if (isEnPassant)
      grid[start.row()][end.col()] = ' ';
  }

  private void executeCastlingIfNeeded(Coordinate start, Coordinate end, char piece) {
    if (Character.toLowerCase(piece) == 'k' && Math.abs(start.col() - end.col()) == 2) {
      int rookRow = start.row();
      if (end.col() > start.col()) {
        grid[rookRow][5] = grid[rookRow][7];
        grid[rookRow][7] = ' ';
      } else {
        grid[rookRow][3] = grid[rookRow][0];
        grid[rookRow][0] = ' ';
      }
    }
  }

  private void movePieceOnGrid(Coordinate start, Coordinate end, char piece) {
    grid[end.row()][end.col()] = piece;
    grid[start.row()][start.col()] = ' ';
  }

  private void executePromotionIfNeeded(Coordinate end, String cleanMove) {
    if (cleanMove.length() == 5) {
      char promotionPiece = cleanMove.charAt(4);
      grid[end.row()][end.col()] = (activeColor == PieceColor.WHITE)
          ? Character.toUpperCase(promotionPiece)
          : Character.toLowerCase(promotionPiece);
    }
  }

  private void updateHalfMoveClock(boolean isPawn, boolean isCapture) {
    if (isPawn || isCapture)
      halfMoveClock = 0;
    else
      halfMoveClock++;
  }

  private void updateEnPassantTarget(Coordinate start, Coordinate end, boolean isPawn) {
    if (isPawn && Math.abs(start.row() - end.row()) == 2) {
      int step = (end.row() - start.row()) / 2;
      Coordinate epCoord = new Coordinate(start.row() + step, start.col());
      enPassantTarget = epCoord.toAlgebraic();
    } else
      enPassantTarget = "-";
  }

  private void updateCastlingRights(Coordinate start, Coordinate end, char piece) {
    if (castlingRights.equals("-"))
      return;

    if (piece == 'K')
      castlingRights = castlingRights.replace("K", "").replace("Q", "");
    if (piece == 'k')
      castlingRights = castlingRights.replace("k", "").replace("q", "");

    if ((start.row() == 7 && start.col() == 7) || (end.row() == 7 && end.col() == 7))
      castlingRights = castlingRights.replace("K", "");
    if ((start.row() == 7 && start.col() == 0) || (end.row() == 7 && end.col() == 0))
      castlingRights = castlingRights.replace("Q", "");
    if ((start.row() == 0 && start.col() == 7) || (end.row() == 0 && end.col() == 7))
      castlingRights = castlingRights.replace("k", "");
    if ((start.row() == 0 && start.col() == 0) || (end.row() == 0 && end.col() == 0))
      castlingRights = castlingRights.replace("q", "");

    if (castlingRights.isEmpty())
      castlingRights = "-";
  }

  private void advanceTurn() {
    if (activeColor == PieceColor.BLACK)
      fullMoveNumber++;

    activeColor = activeColor.opposite();
  }
}
