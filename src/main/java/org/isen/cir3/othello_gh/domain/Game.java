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
    private int winner;

    @Column
    private int white;

    @Column
    private int black;

    @Lob
    private CellStatus[][] board;

    public Game(){}
    public Game(Integer size) {
        board = new CellStatus[size-1][size-1];
    }
}
