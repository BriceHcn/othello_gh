package org.isen.cir3.othello_gh.service;

import org.isen.cir3.othello_gh.domain.CellStatus;
import org.isen.cir3.othello_gh.domain.Game;
import org.isen.cir3.othello_gh.domain.GameStatus;
import org.isen.cir3.othello_gh.exception.InvalidMoveException;
import org.isen.cir3.othello_gh.repository.GameRepository;
import org.isen.cir3.othello_gh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        game.setScoreBlack(2);
        game.setScoreWhite(2);
        //setup du joueur qui commence
        game.setStatus(GameStatus.BLACK_TURN);
        //setup du plateau
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                setCell(game, row, col, CellStatus.EMPTY);
            }
        }
        //plateau de base
        game.getBoard()[((size/2)-1)][((size/2)-1)]=CellStatus.W;
        game.getBoard()[(size/2)][(size/2)]=CellStatus.W;
        game.getBoard()[(size/2)-1][(size/2)]=CellStatus.B;
        game.getBoard()[(size/2)][((size/2)-1)]=CellStatus.B;
        return game;
    }

    private void setCell(Game game, int column, int row, CellStatus value) {
        game.getBoard()[column][row] = value;
    }

    public int checkScore(Game game, CellStatus cell){
        int score = 0;
        for(int i = 0;i<game.getBoard().length;i++){
            for(int a = 0;a<game.getBoard().length;a++){
                if(game.getBoard()[i][a].toString().equals(cell.toString())){
                    score=score+1;
                }

            }
        }
        return score;
    }

    public Object findGameForCurrentUser( Pageable pageable) {
        List<Game> gamesForSpecificUser= new ArrayList();
        for(Game g : games.findAll(pageable)){
            if((users.findById((long) g.getWhite()).get().getUsername()).equals(userService.getConnectedUserUsername())  || (users.findById((long) g.getBlack()).get().getUsername()).equals(userService.getConnectedUserUsername())){
                gamesForSpecificUser.add(g);
            }
        }
        Page<Game> test= new PageImpl<>(gamesForSpecificUser);

        return test;
    }

    public Game play(Game game, int row, int column) throws InvalidMoveException {
        int cur = game.getCurrentPlayer();
        int b = game.getBlack();
        //si c'est le tour blanc
        if(cur == b){
            //on voit si le moove est valide
            try {
                isMoveValid(game, column, row, CellStatus.B, CellStatus.W);
            }catch (InvalidMoveException e){
                throw new InvalidMoveException();
            }
        }
        //si c'est tour noir
        else{
            try {
                isMoveValid(game, column, row, CellStatus.W, CellStatus.B);
            }catch (InvalidMoveException e){
                throw new InvalidMoveException();
            }
        }

        //si le coup est valide on place un pion de la bonn couleur
        if(cur == b){
            setCell(game, column, row, CellStatus.B);
        }
        else{
            setCell(game, column, row, CellStatus.W);
        }





        // if the game has not ended, alternate player
        alternatePlayer(game);
        //on actualise les scores
        game.setScoreWhite(checkScore(game, CellStatus.W));
        game.setScoreBlack(checkScore(game, CellStatus.B));




        game=checkWinner(game);
        game= checkDraw(game);
        /*
        if (game.getWinner() != game.getBlack() || game.getWinner() != game.getWhite()) {

        }

         */
        return game;
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

    private Game checkDraw(Game game) {
        int scoreB = 0;
        int scoreW = 0;
        int scoreE = 0;
        int size = game.getBoard().length;
        int nbCase = size*size;
        for(int i = 0;i<size;i++){
            for(int a = 0;a<size;a++){
                if(game.getBoard()[i][a].toString().equals(CellStatus.B.toString())){
                    scoreB=scoreB+1;
                }
                else if(game.getBoard()[i][a].toString().equals(CellStatus.W.toString())){
                    scoreW=scoreW+1;
                }else if(game.getBoard()[i][a].toString().equals(CellStatus.EMPTY.toString())){
                    scoreE=scoreE+1;
                }
            }
        }
        if(scoreE==0 && scoreB==scoreW){
            game.setStatus(GameStatus.DRAW);
            return  game;

        }
        return game;
    }

    private Game checkWinner(Game game) {
        int scoreB = 0;
        int scoreW = 0;
        int scoreE = 0;
        int size = game.getBoard().length;
        int nbCase = size*size;
        for(int i = 0;i<size;i++){
            for(int a = 0;a<size;a++){
                if(game.getBoard()[i][a].toString().equals(CellStatus.B.toString())){
                    scoreB=scoreB+1;
                }
                else if(game.getBoard()[i][a].toString().equals(CellStatus.W.toString())){
                    scoreW=scoreW+1;
                }else if(game.getBoard()[i][a].toString().equals(CellStatus.EMPTY.toString())){
                    scoreE=scoreE+1;
                }
            }
        }
        if(scoreE==0){
            if(scoreB>scoreE){
                game.setWinner(game.getBlack());
                game.setStatus(GameStatus.BLACK_WIN);
                return game;

            }
            game.setWinner(game.getWhite());
            game.setStatus(GameStatus.WHITE_WIN);
            return  game;

        }
    return game;
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
    private boolean isMoveValid(Game game, int col ,int row,CellStatus player,CellStatus opp) throws InvalidMoveException{
        int size= game.getBoard().length;
        //definition des directions de recherches
        int droite = game.getBoard().length-col-1;
        int bas = game.getBoard().length-row-1;
        int gauche = game.getBoard().length-(game.getBoard().length-col-1)-1;
        int haut = game.getBoard().length-(game.getBoard().length-row-1)-1;




        //presence d'une autre piece autour qui est un adversaire
        int enemyHautGauche = 0;
        //  il faut verifier si la case existe
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

        //verif en haut a gauche
        int maCouleurHautGauche = 0;
        if(enemyHautGauche==1){
            for(int i = gauche-1;i>=0;i--){
                for(int e = haut-1;e>=0;e--){
                    if(game.getBoard()[i][e].toString().equals(player.toString())){
                        maCouleurHautGauche++;
                        break;
                    }
                }
            }
        }
        //si j'ai trouvé en haut a gauche
        if(maCouleurHautGauche != 0 && enemyHautGauche ==1){
        for(int i =0;i<=maCouleurHautGauche;i++){
                setCell(game, col-i, row-i, player);

            }
        }

        //verif en bas a droite
        int maCouleurBasDroit=0;
        if(enemyBasDroite==1){
            for(int i =col;i<size-col;i++){
                for(int e=row;e<size-row;e++){
                    if(game.getBoard()[i][e].toString().equals(player.toString())){
                        maCouleurBasDroit++;
                        break;
                    }
                }
            }
        }
        //si je des collegues en bas a droite
        if(enemyBasDroite!=0 && enemyBasDroite==1){
                for(int e = 0;e<=maCouleurBasDroit;e++){
                    setCell(game, col+e, row+e, player);
            }
        }


        //verif en haut a droite
        int maCouleurHautDroit = 0;
        if(enemyHautDroit==1){
            for(int i = col;i<size-col;i++){
                for(int e = row;e>=0;e--){
                    if(game.getBoard()[i][e].toString().equals(player.toString())){
                        maCouleurHautDroit++;
                        break;
                    }
                }
            }
        }
        //si je trouve un collegue en haut a droite
        if(maCouleurHautDroit!=0 && enemyHautDroit==1){
            for(int i = 0;i<=maCouleurHautDroit;i++){
                setCell(game, col+i, row-i, player);
            }
        }

        int maCouleurBasGauche=0;
        if(enemyBasGauche==1){
            for(int i = col;i>=0;i--){
                for(int e = row;e<size-row;e++){
                    if(game.getBoard()[i][e].toString().equals(player.toString())){
                        maCouleurBasGauche++;
                        break;
                    }
                }
            }
        }
        System.out.println(enemyBasGauche);
        //si je trouve un collegue en bas a gauche
        if(maCouleurBasGauche!=0 && enemyBasGauche==1){
            for(int i = 0;i<maCouleurBasGauche;i++){
                setCell(game, col-i, row+i, player);
            }
        }

        //si au final mes recherches se sont montrés infructueuse c'est que le mouve ne capturais aucune piece dans aucun sens
if(maCouleurBas== 0 && maCouleurDroite == 0 && maCouleurGauche ==0 && maCouleurHaut==0 && maCouleurHautGauche ==0 && maCouleurBasDroit==0 && maCouleurHautDroit==0 && maCouleurBasGauche==0){
    throw new InvalidMoveException();
}
        return true;
    }

}
