package com.javiervidgar.chess_backend.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.javiervidgar.chess_backend.domain.valueobject.GameState;
import com.javiervidgar.chess_backend.domain.valueobject.Move;
import com.javiervidgar.chess_backend.domain.valueobject.PieceColor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Game {

  private Long id;

  private Long whitePlayerId;
  private Long blackPlayerId;

  private String currentFen;

  @Builder.Default
  private List<Move> moves = new ArrayList<>();

  private GameState state;
  private PieceColor turn;

  public void addMove(Move move) {
    this.moves.add(move);
    this.currentFen = move.resultingFen();
    this.turn = this.turn.opposite();
  }

  public void finishGame(GameState finalState) {
    this.state = finalState;
  }

}
