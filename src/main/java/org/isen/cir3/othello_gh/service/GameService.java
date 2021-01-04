package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.isen.cir3.othello_gh.domain.User;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    UserRepository users;

    @Autowired
    GameRepository games;

    @Autowired
    UserService userService;



    public Game create(int size, int J1, int J2) {
        Game game = new Game(size);
        game.setBlack(J1);//todo randomisation des joueurs
        game.setWhite(J2);
        game.setStatus(GameStatus.BLACK_TURN);
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

    public Object findGameForCurrentUser() {
        List<Game> gamesForSpecificUser= new ArrayList<Game>();
        for(Game g : games.findAll()){
            if((users.findById((long) g.getWhite()).get().getUsername()).equals(userService.getConnectedUserUsername())  || (users.findById((long) g.getBlack()).get().getUsername()).equals(userService.getConnectedUserUsername())){
                gamesForSpecificUser.add(g);
            }
        }
        return gamesForSpecificUser;
    }
}
