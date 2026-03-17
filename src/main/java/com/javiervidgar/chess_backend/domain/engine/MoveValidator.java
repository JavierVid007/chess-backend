package com.javiervidgar.chess_backend.domain.engine;

import com.javiervidgar.chess_backend.domain.valueobject.Coordinate;
import com.javiervidgar.chess_backend.domain.valueobject.PieceColor;

public class MoveValidator {

  public static boolean isLegalMove(Board board, String moveNotation) {
    String cleanMove = moveNotation.replace("-", "");
    Coordinate start = Coordinate.fromAlgebraic(cleanMove.substring(0, 2));
    Coordinate end = Coordinate.fromAlgebraic(cleanMove.substring(2, 4));

    char piece = board.getGrid()[start.row()][start.col()];
    if (piece == ' ')
      return false;

    boolean isWhitePiece = Character.isUpperCase(piece);

    if (!isCorrectTurn(board, isWhitePiece))
      return false;
    if (isFriendlyCapture(board, end, isWhitePiece))
      return false;

    if (!isPseudoLegal(board, start, end, piece, isWhitePiece))
      return false;

    if (isInvalidCastlingAttempt(board, start, end, piece, isWhitePiece))
      return false;

    return !leavesKingInCheck(board, start, end, piece, isWhitePiece);
  }

  public static boolean hasAnyLegalMove(Board board) {
    boolean isWhiteTurn = board.getActiveColor() == PieceColor.WHITE;

    for (int startRow = 0; startRow < 8; startRow++) {
      for (int startCol = 0; startCol < 8; startCol++) {
        char piece = board.getGrid()[startRow][startCol];
        if (piece == ' ' || Character.isUpperCase(piece) != isWhiteTurn)
          continue;

        Coordinate start = new Coordinate(startRow, startCol);
        String startAlg = start.toAlgebraic();

        for (int endRow = 0; endRow < 8; endRow++) {
          for (int endCol = 0; endCol < 8; endCol++) {
            String moveNotation = startAlg + new Coordinate(endRow, endCol).toAlgebraic();
            if (isLegalMove(board, moveNotation))
              return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean isKingInCheck(Board board, boolean isWhiteTurn) {
    Coordinate kingPos = findKing(board, isWhiteTurn);
    if (kingPos == null)
      return false;
    return isSquareAttacked(board, kingPos, !isWhiteTurn);
  }

  private static boolean isCorrectTurn(Board board, boolean isWhitePiece) {
    return (board.getActiveColor() == PieceColor.WHITE && isWhitePiece) ||
        (board.getActiveColor() == PieceColor.BLACK && !isWhitePiece);
  }

  private static boolean isFriendlyCapture(Board board, Coordinate end, boolean isWhitePiece) {
    char targetPiece = board.getGrid()[end.row()][end.col()];
    if (targetPiece == ' ')
      return false;
    return Character.isUpperCase(targetPiece) == isWhitePiece;
  }

  private static boolean leavesKingInCheck(Board board, Coordinate start, Coordinate end, char piece,
      boolean isWhitePiece) {
    char[][] originalGrid = copyGrid(board.getGrid());

    board.getGrid()[end.row()][end.col()] = piece;
    board.getGrid()[start.row()][start.col()] = ' ';

    Coordinate myKingPos = findKing(board, isWhitePiece);
    boolean inCheck = myKingPos != null && isSquareAttacked(board, myKingPos, !isWhitePiece);

    board.setGrid(originalGrid);

    return inCheck;
  }

  private static boolean isPseudoLegal(Board board, Coordinate start, Coordinate end, char piece,
      boolean isWhitePiece) {
    return switch (Character.toLowerCase(piece)) {
      case 'p' -> isValidPawnMove(board, start, end, isWhitePiece);
      case 'r' -> isValidRookMove(board, start, end);
      case 'n' -> isValidKnightMove(start, end);
      case 'b' -> isValidBishopMove(board, start, end);
      case 'q' -> isValidQueenMove(board, start, end);
      case 'k' -> isValidKingMove(board, start, end, isWhitePiece);
      default -> false;
    };
  }

  private static boolean isValidPawnMove(Board board, Coordinate start, Coordinate end, boolean isWhite) {
    int direction = isWhite ? -1 : 1;
    int startRow = isWhite ? 6 : 1;
    char targetPiece = board.getGrid()[end.row()][end.col()];

    if (end.col() == start.col() && end.row() == start.row() + direction)
      return targetPiece == ' ';
    if (end.col() == start.col() && start.row() == startRow && end.row() == start.row() + (2 * direction)) {
      char intermediatePeice = board.getGrid()[start.row() + direction][start.col()];
      return intermediatePeice == ' ' && targetPiece == ' ';
    }
    if (Math.abs(end.col() - start.col()) == 1 && end.row() == start.row() + direction) {
      if (targetPiece != ' ')
        return true;
      String epTarget = board.getEnPassantTarget();
      return !"-".equals(epTarget) && end.toAlgebraic().equals(epTarget);
    }
    return false;
  }

  private static boolean isValidKnightMove(Coordinate start, Coordinate end) {
    int rowDiff = Math.abs(start.row() - end.row());
    int colDiff = Math.abs(start.col() - end.col());
    return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
  }

  private static boolean isValidRookMove(Board board, Coordinate start, Coordinate end) {
    if (start.row() != end.row() && start.col() != end.col())
      return false;
    return isPathClear(board, start, end);
  }

  private static boolean isValidBishopMove(Board board, Coordinate start, Coordinate end) {
    if (Math.abs(start.row() - end.row()) != Math.abs(start.col() - end.col()))
      return false;
    return isPathClear(board, start, end);
  }

  private static boolean isValidQueenMove(Board board, Coordinate start, Coordinate end) {
    return isValidRookMove(board, start, end) || isValidBishopMove(board, start, end);
  }

  private static boolean isValidKingMove(Board board, Coordinate start, Coordinate end, boolean isWhite) {
    int rowDiff = Math.abs(end.row() - start.row());
    int colDiff = Math.abs(end.col() - start.col());

    if (rowDiff <= 1 && colDiff <= 1)
      return true;

    if (rowDiff == 0 && colDiff == 2) {
      int homeRow = isWhite ? 7 : 0;
      if (start.row() != homeRow || start.col() != 4)
        return false;

      String rights = board.getCastlingRights();
      if (end.col() > start.col())
        return rights.indexOf(isWhite ? 'K' : 'k') != -1 && isPathClear(board, start, new Coordinate(homeRow, 7));
      else
        return rights.indexOf(isWhite ? 'Q' : 'q') != -1 && isPathClear(board, start, new Coordinate(homeRow, 0));
    }
    return false;
  }

  private static boolean isInvalidCastlingAttempt(Board board, Coordinate start, Coordinate end, char piece,
      boolean isWhitePiece) {
    if (Character.toLowerCase(piece) != 'k' || Math.abs(start.col() - end.col()) != 2)
      return false;

    boolean opponentIsWhite = !isWhitePiece;
    if (isSquareAttacked(board, start, opponentIsWhite))
      return true;
    if (isSquareAttacked(board, end, opponentIsWhite))
      return true;

    int intermediateCol = start.col() + (end.col() > start.col() ? 1 : -1);
    Coordinate intermediateSquare = new Coordinate(start.row(), intermediateCol);
    return isSquareAttacked(board, intermediateSquare, opponentIsWhite);
  }

  private static boolean isSquareAttacked(Board board, Coordinate square, boolean byWhite) {
    return isAttackedByKnight(board, square, byWhite) ||
        isAttackedByStraight(board, square, byWhite) ||
        isAttackedByDiagonal(board, square, byWhite) ||
        isAttackedByPawn(board, square, byWhite) ||
        isAttackedByKing(board, square, byWhite);
  }

  private static boolean isAttackedByKnight(Board board, Coordinate square, boolean byWhite) {
    int[][] knightMoves = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 } };
    char enemyKnight = byWhite ? 'N' : 'n';
    for (int[] move : knightMoves) {
      int nr = square.row() + move[0], nc = square.col() + move[1];
      if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && board.getGrid()[nr][nc] == enemyKnight)
        return true;
    }
    return false;
  }

  private static boolean isAttackedByStraight(Board board, Coordinate square, boolean byWhite) {
    int[][] straightDirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    return isAttackedInDirections(board, square.row(), square.col(), straightDirs, byWhite ? 'R' : 'r',
        byWhite ? 'Q' : 'q');
  }

  private static boolean isAttackedByDiagonal(Board board, Coordinate square, boolean byWhite) {
    int[][] diagDirs = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
    return isAttackedInDirections(board, square.row(), square.col(), diagDirs, byWhite ? 'B' : 'b',
        byWhite ? 'Q' : 'q');
  }

  private static boolean isAttackedByPawn(Board board, Coordinate square, boolean byWhite) {
    int pawnDir = byWhite ? 1 : -1;
    char enemyPawn = byWhite ? 'P' : 'p';
    for (int colOffset : new int[] { -1, 1 }) {
      int pr = square.row() + pawnDir, pc = square.col() + colOffset;
      if (pr >= 0 && pr < 8 && pc >= 0 && pc < 8 && board.getGrid()[pr][pc] == enemyPawn)
        return true;
    }
    return false;
  }

  private static boolean isAttackedByKing(Board board, Coordinate square, boolean byWhite) {
    char enemyKing = byWhite ? 'K' : 'k';
    for (int r = -1; r <= 1; r++) {
      for (int c = -1; c <= 1; c++) {
        if (r == 0 && c == 0)
          continue;
        int nr = square.row() + r, nc = square.col() + c;
        if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && board.getGrid()[nr][nc] == enemyKing)
          return true;
      }
    }
    return false;
  }

  private static boolean isAttackedInDirections(Board board, int r, int c, int[][] dirs, char piece1, char piece2) {
    for (int[] dir : dirs) {
      int nr = r + dir[0], nc = c + dir[1];
      while (nr >= 0 && nr < 8 && nc >= 0 && nc < 8) {
        char p = board.getGrid()[nr][nc];
        if (p != ' ') {
          if (p == piece1 || p == piece2)
            return true;
          break;
        }
        nr += dir[0];
        nc += dir[1];
      }
    }
    return false;
  }

  private static boolean isPathClear(Board board, Coordinate start, Coordinate end) {
    int rowStep = Integer.signum(end.row() - start.row());
    int colStep = Integer.signum(end.col() - start.col());

    int currentRow = start.row() + rowStep;
    int currentCol = start.col() + colStep;

    while (currentRow != end.row() || currentCol != end.col()) {
      if (board.getGrid()[currentRow][currentCol] != ' ')
        return false;
      currentRow += rowStep;
      currentCol += colStep;
    }
    return true;
  }

  private static char[][] copyGrid(char[][] original) {
    char[][] copy = new char[8][8];
    for (int i = 0; i < 8; i++) {
      System.arraycopy(original[i], 0, copy[i], 0, 8);
    }
    return copy;
  }

  private static Coordinate findKing(Board board, boolean isWhite) {
    char kingChar = isWhite ? 'K' : 'k';
    for (int r = 0; r < 8; r++)
      for (int c = 0; c < 8; c++)
        if (board.getGrid()[r][c] == kingChar)
          return new Coordinate(r, c);
    return null;
  }
}
