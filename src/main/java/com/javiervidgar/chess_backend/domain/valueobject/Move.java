package com.javiervidgar.chess_backend.domain.valueobject;

public record Move(
    int moveNumber,
    PieceColor playerColor,
    String notation,
    String resultingFen) {

}
