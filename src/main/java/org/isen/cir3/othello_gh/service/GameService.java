package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    public Game create() {
        Game game = new Game();

        game.setStatus(GameStatus.STARTED);

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                setCell(game, row, col, CellStatus.EMPTY);
            }
        }

        return game;
    }
    private void setCell(Game game, int row, int column, CellStatus value) {
        game.getBoard()[row][column] = value;
    }
}
