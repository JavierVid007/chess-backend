package com.javiervidgar.chess_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {

  private Long id;

  private String username;
  private String hashedPassword;

  private int eloRating;

  public void updatePassword(String newHashedPassword) {
    this.hashedPassword = newHashedPassword;
  }

  public void updateElo(int newEloRating) {
    this.eloRating = newEloRating;
  }

}
