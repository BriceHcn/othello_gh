package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.isen.cir3.othello_gh.exception.InvalidMoveException;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        //setup des roles
        Game game = new Game(size);
        if(Math.random() < 0.5){
            game.setBlack(J1);
            game.setWhite(J2);
        }else{
            game.setBlack(J2);
            game.setWhite(J1);
        }
        game.setCurrentPlayer(game.getBlack());
        //setup du joueur qui commence
        game.setStatus(GameStatus.BLACK_TURN);

        //setup du plateau
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                setCell(game, row, col, CellStatus.EMPTY);
            }
        }
        game.getBoard()[((size/2)-1)][((size/2)-1)]=CellStatus.W;
        game.getBoard()[(size/2)][(size/2)]=CellStatus.W;
        game.getBoard()[(size/2)-1][(size/2)]=CellStatus.B;
        game.getBoard()[(size/2)][((size/2)-1)]=CellStatus.B;
        return game;
    }

    private void setCell(Game game, int column, int row, CellStatus value) {
        game.getBoard()[column][row] = value;
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

    public Game play(Game game, int row, int column) throws InvalidMoveException {
        int cur = game.getCurrentPlayer();
        int b = game.getBlack();
        int w = game.getWhite();


        if(cur == b){
            try {
                isMoveValid(game, cur, b, w, column, row, CellStatus.B, CellStatus.W);
            }catch (InvalidMoveException e){
                throw new InvalidMoveException();
            }
        }
        else{
            try {
                isMoveValid(game, cur, b, w, column, row, CellStatus.W, CellStatus.B);
            }catch (InvalidMoveException e){
                throw new InvalidMoveException();
            }
        }


        if(cur == b){
            setCell(game, column, row, CellStatus.B);
        }
        else{
            setCell(game, column, row, CellStatus.W);
        }


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

    private boolean isMoveValid(Game game,int cur, int black, int white, int col ,int row,CellStatus player,CellStatus opp) throws InvalidMoveException{
        int size= game.getBoard().length;
        //definition des directions de recherches
        int droite = game.getBoard().length-col-1;
        int bas = game.getBoard().length-row-1;
        int gauche = game.getBoard().length-(game.getBoard().length-col-1)-1;
        int haut = game.getBoard().length-(game.getBoard().length-row-1)-1;




        //presence d'une autre piece autour qui est un adversaire
        // si la case existe
        int enemyHautGauche = 0;
        if(0<=col-1 && 0<=row-1){
            //et que cette case est un adversaire
            if(game.getBoard()[col-1][row-1].toString().equals(opp.toString())){
                enemyHautGauche = 1;
            }
        }
        int enemyHautMilieu = 0;
        if(0<=row-1){
            //et que cette case est un adversaire
            if(game.getBoard()[col][row-1].toString().equals(opp.toString())){
                enemyHautMilieu = 1;
            }
        }
        int enemyHautDroit = 0;
        if(0<=row-1 && col+1<=size-1){
            //et que cette case est un adversaire
            if(game.getBoard()[col+1][row-1].toString().equals(opp.toString())){
                enemyHautDroit = 1;
            }
        }
        int enemyGauche = 0;
        if(0<=col-1){
            //et que cette case est un adversaire
            if(game.getBoard()[col-1][row].toString().equals(opp.toString())){
                enemyGauche = 1;
            }
        }
        int enemyBasGauche = 0;
        if(row+1<=size-1 && col-1>=0){
            //et que cette case est un adversaire
            if(game.getBoard()[col-1][row+1].toString().equals(opp.toString())){
                enemyBasGauche = 1;
            }
        }
        int enemyBas = 0;
        if(row+1<=size-1){
            //et que cette case est un adversaire
            if(game.getBoard()[col][row+1].toString().equals(opp.toString())){
                enemyBas = 1;
            }
        }

        int enemyDroite = 0;
        if(col+1<=size-1 ){
            //et que cette case est un adversaire
            if(game.getBoard()[col+1][row].toString().equals(opp.toString())){
                enemyDroite = 1;
            }
        }
        int enemyBasDroite = 0;
        if(col+1<=size-1 && row+1<=size-1 ){
            //et que cette case est un adversaire
            if(game.getBoard()[col+1][row+1].toString().equals(opp.toString())){
                enemyBasDroite = 1;
            }
        }
        if(enemyHautGauche==0 && enemyHautMilieu==0 && enemyHautDroit==0 && enemyGauche==0 && enemyDroite==0 && enemyBasGauche == 0 && enemyBas==0 && enemyBasDroite==0){
            throw new InvalidMoveException();
        }


        //Maintenant il faut parcourir chaque direction ou j'ai trouvé des enemis pour voir si je trouve un collegue si oui je prend les pions entre nous
        //verif vers la droite
        int maCouleurDroite = 0;
        if(enemyDroite == 1){
            for(int i=col;i<size;i++) {
                if (game.getBoard()[i][row].toString().equals(player.toString())) {
                    maCouleurDroite = i-col;
                    break;
                }
            }
        }
        //si j'ai trouvé des "collegues" a droite
        if(maCouleurDroite !=0 && enemyDroite == 1){
            for(int i=col;i<=col+maCouleurDroite;i++) {
                setCell(game, i, row, player);
            }
        }

        //verif vers le bas
        int maCouleurBas = 0;
        if(enemyBas == 1){
            for(int i=row;i<size;i++) {
                if (game.getBoard()[col][i].toString().equals(player.toString())) {
                    maCouleurBas = i-row;
                    break;
                }
            }
        }
        //si j'ai trouvé des "collegues" en bas
        if(maCouleurBas !=0 && enemyBas==1){
            for(int i=row;i<=row+maCouleurBas;i++) {
                setCell(game, col, i, player);
            }
        }

        //verif vers la gauche
        int maCouleurGauche = 0;
        if(enemyGauche == 1){
            for(int i=gauche-1;i>=0;i--) {
                if (game.getBoard()[i][row].toString().equals(player.toString())) {
                    maCouleurGauche = Math.abs(i-col)-1;
                    break;
                }
            }
        }
        //si j'ai trouvé des collegues a guache
        if(maCouleurGauche !=0 && enemyGauche==1){
            for(int i=col;i>maCouleurGauche+1;i--) {
                setCell(game, i, row, player);
            }
        }

        //verif vers le haut
        int maCouleurHaut=0;
        if(enemyHautMilieu ==1){
            for(int i=haut-1;i>=0;i--){
                if (game.getBoard()[col][i].toString().equals(player.toString())) {
                    maCouleurHaut = Math.abs(i-row)-1;
                    break;
                }
            }
        }
        //si j'ai trouvé des collegues en haut
        if(maCouleurHaut!=0 && enemyHautMilieu==1){
            for(int i=row;i>maCouleurHaut+1;i--) {
                setCell(game, col, i, player);
            }
        }
        //todo continuer ici
        //si au final mes recherches se sont montrés infructueuse c'est que le mouve ne capturais aucune piece
if(maCouleurBas== 0 && maCouleurDroite == 0 && maCouleurGauche ==0 && maCouleurHaut==0){//todo a completer
    throw new InvalidMoveException();
}

        return true;
    }









    private void alternatePlayer(Game game) {
        int cur = game.getCurrentPlayer();
        int b = game.getBlack();
        int w = game.getWhite();
        if(cur == b){
            game.setCurrentPlayer(w);

            game.setStatus(GameStatus.WHITE_TURN);
        }
        else{
            game.setCurrentPlayer(b);
            game.setStatus(GameStatus.BLACK_TURN);
        }
    }

    private void checkDraw(Game game) {
    }

    private void checkWinner(Game game) {
    }

    public boolean isCaseEmpty(Game game,int col,int row){
        if((game.getBoard()[row][col].toString()).equals(CellStatus.EMPTY.toString())){
            return true;
        }else{
            return false;
        }
    }
    public boolean canIPlay(String connectedUserUsername,Game game) {
        if(((long)game.getCurrentPlayer() == users.findByUsername(connectedUserUsername).getId())){
            return true;
        }
        else{
            return false;
        }

    }
}
