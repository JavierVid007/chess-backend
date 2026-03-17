package com.javiervidgar.chess_backend.domain.port.in;

import com.javiervidgar.chess_backend.domain.model.Game;

public interface MakeMoveUseCase {

  Game execute(String gameId, String moveNotation);
}
