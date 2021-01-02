package org.isen.cir3.othello_gh.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column
    private User winner;

    @Column
    private User white;

    @Column
    private User black;

    @Column
    private User currentPlayer;

    @Lob
    private CellStatus[][] board;

    public Game() {
        board = new CellStatus[8][8];
    }
}
