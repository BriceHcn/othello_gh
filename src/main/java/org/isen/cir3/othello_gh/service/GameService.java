package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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
        game.setCurrentPlayer(J1);//TODO celui qui a les noirs du coup, pas forcement j1
        game.setStatus(GameStatus.BLACK_TURN);
        //game.setWinner(null);

        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
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


    public Game play(Game game, int row, int column) {
        // ok, so this is a valid move, update board
        setCell(game, row, column, CellStatus.B);

        // check if there is a winner
        //checkWinner(game);

        // no winner ? then check if this is a draw
        //if (game.getWinner()== null) {
            //checkDraw(game);
        //}

        // if the game has not ended, alternate player
        alternatePlayer(game);

        return game;
    }

    private void alternatePlayer(Game game) {
        int cur = game.getCurrentPlayer();
        int b = game.getBlack();
        int w = game.getWhite();
        if(cur == b){
            game.setCurrentPlayer(w);
        }
        else{
            game.setCurrentPlayer(b);
        }
    }

    private void checkDraw(Game game) {
    }

    private void checkWinner(Game game) {
    }

    public boolean canIplay(String connectedUserUsername,Game game) {
        if((long)game.getCurrentPlayer() == users.findByUsername(connectedUserUsername).getId()){
            return true;
        }
        return false;
    }
}
