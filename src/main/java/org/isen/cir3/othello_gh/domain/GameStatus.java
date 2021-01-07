package org.isen.cir3.othello_gh.domain;

public enum GameStatus {
     DRAW("Egalité"), BLACK_TURN("Tour Noir"), WHITE_TURN("Tour Blanc"), BLACK_WIN("Victoire Noir"), WHITE_WIN("Victoire Blanc");
     public final String string;

     private GameStatus(String string) {
          this.string = string;
     }

     @Override
     public String toString() {
          return string;
     }
}
