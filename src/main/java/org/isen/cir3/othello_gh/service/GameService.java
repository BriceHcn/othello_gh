package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.springframework.stereotype.Service;

@Service
public class GameService {



    public Game create(int size, int idJ1, int idJ2) {
        Game game = new Game(size);

        game.setStatus(GameStatus.STARTED);
        game.setBlack(idJ1);//todo randomisation des joueurs
        game.setWhite(idJ2);
        game.setCurrentPlayer(game.getBlack());
        //game.setWinner(null);

        for (int row = 0; row < size-1; ++row) {
            for (int col = 0; col < size-1; ++col) {
                setCell(game, row, col, CellStatus.EMPTY);
            }
        }

        return game;
    }
    private void setCell(Game game, int row, int column, CellStatus value) {
        game.getBoard()[row][column] = value;
    }
}
