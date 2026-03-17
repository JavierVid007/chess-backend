package com.javiervidgar.chess_backend.domain.engine;

import com.javiervidgar.chess_backend.domain.valueobject.PieceColor;

public class ChessEngine {

  public record EngineResult(String newFen, boolean isCheckmante, boolean isStalemate) {
  }

  public EngineResult processMove(String currentFen, String moveNotation) {
    Board board = FenUtility.fromFen(currentFen);

    validateMove(board, moveNotation);

    board.makeMove(moveNotation);

    return evaluateGameState(board);
  }

  private void validateMove(Board board, String moveNotation) {
    if (!MoveValidator.isLegalMove(board, moveNotation))
      throw new IllegalArgumentException("Movimiento ilegal: " + moveNotation);
  }

  private EngineResult evaluateGameState(Board board) {
    boolean isWhiteTurn = board.getActiveColor() == PieceColor.WHITE;
    boolean hasMoves = MoveValidator.hasAnyLegalMove(board);
    boolean isKingInCheck = MoveValidator.isKingInCheck(board, isWhiteTurn);

    boolean isCheckmate = !hasMoves && isKingInCheck;
    boolean isStalemate = !hasMoves && !isKingInCheck;

    if (board.getHalfMoveClock() >= 100)
      isStalemate = true;

    String resultingFen = FenUtility.toFen(board);

    return new EngineResult(resultingFen, isCheckmate, isStalemate);
  }
}
