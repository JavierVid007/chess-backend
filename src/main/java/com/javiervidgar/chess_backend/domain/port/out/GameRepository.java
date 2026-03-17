package com.javiervidgar.chess_backend.domain.port.out;

import java.util.Optional;

import com.javiervidgar.chess_backend.domain.model.Game;

public interface GameRepository {

  void save(Game game);

  Optional<Game> findById(String id);
}
